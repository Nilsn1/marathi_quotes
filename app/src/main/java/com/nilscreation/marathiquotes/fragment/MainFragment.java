package com.nilscreation.marathiquotes.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.rvadapter.AdmobNativeAdAdapter;
import com.nilscreation.marathiquotes.BloggerApiService;
import com.nilscreation.marathiquotes.R;
import com.nilscreation.marathiquotes.SplashActivity;
import com.nilscreation.marathiquotes.adapter.QuoteAdapter;
import com.nilscreation.marathiquotes.model.APIConfig;
import com.nilscreation.marathiquotes.model.BloggerModel;
import com.nilscreation.marathiquotes.model.QuoteModel;
import com.nilscreation.marathiquotes.model.SharedViewModel;
import com.nilscreation.marathiquotes.service.AdConfig;
import com.nilscreation.marathiquotes.service.AppConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment {
    //    TextView title;
    private RecyclerView recyclerView;
    private List<QuoteModel> factslist;
    private List<String> labelsList;
    String category;
    QuoteAdapter adapter;

    ProgressBar progressBar;
    private AdView mAdView;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

//        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        progressBar = view.findViewById(R.id.progressBar);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

//        title = view.findViewById(R.id.title);

        factslist = new ArrayList<>();
        fetchData();

        Bundle bundle = getArguments();
        if (bundle != null) {
            category = bundle.getString("Category");
//            title.setText(category);
        } else {
            category = "All";
//            title.setText(getString(R.string.app_name));
        }

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        boolean showAds = AppConfig.getInstance().isShowAds();

        if (showAds) {
            AdView adView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
        }
        return view;
    }

    private void fetchData() {

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        progressBar.setVisibility(View.VISIBLE); // ⏳ Show loading

        BloggerApiService apiService = retrofit.create(BloggerApiService.class);
        Call<BloggerModel> call = apiService.getBlogPosts();
        call.enqueue(new Callback<BloggerModel>() {
            @Override
            public void onResponse(Call<BloggerModel> call, Response<BloggerModel> response) {

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE); // ✅ Hide after loading

                    BloggerModel mylist = response.body();

                    factslist = extractmyList(mylist.getContent(), getContext());

                    labelsList = mylist.getLabels();

                    if (isAdded()) {
                        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                        viewModel.setLabelsList(labelsList);
                    }

//                    Toast.makeText(getContext(), factslist.get(0).getTitle(), Toast.LENGTH_SHORT).show();
                    adapter = new QuoteAdapter(getContext(), factslist, getActivity());


                    if (AppConfig.getInstance().getNativeAdInterval() > 1) {
                        AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with("ca-app-pub-3940256099942544/2247696110", adapter, "medium").adItemInterval(AppConfig.getInstance().getNativeAdInterval()).build();
                        recyclerView.setAdapter(admobNativeAdAdapter);
                    } else {
                        recyclerView.setAdapter(adapter);
                    }


                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<BloggerModel> call, Throwable t) {

            }
        });
    }


    public static List<QuoteModel> extractmyList(String htmlContent, Context context) {
        List<QuoteModel> myList = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);

        // Select all <h3> tags as categories and <h4> tags as content
        Elements h3Elements = doc.select("h4"); // Select all <h3> tags
        Elements h4Elements = doc.select("h3"); // Select all <h4> tags

        // Loop through each <h3> and <h4> tag pair
        int maxElements = Math.min(h3Elements.size(), h4Elements.size());
        for (int i = 0; i < maxElements; i++) {
            String category = h3Elements.get(i).text(); // Category from <h3>
            String content = h4Elements.get(i).text();  // Content from <h4>

            // Create QuoteModel with category and content
            QuoteModel quoteModel = new QuoteModel(content, category);

            myList.add(quoteModel);
        }

        return myList;
    }
}