package com.nilscreation.marathiquotes.fragment;

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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nilscreation.marathiquotes.R;
import com.nilscreation.marathiquotes.adapter.CategoryAdapter;
import com.nilscreation.marathiquotes.model.SharedViewModel;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    RecyclerView recyclerview;
    ArrayList<String> categorylist;
    CategoryAdapter adapter;
    ProgressBar progressBar;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        recyclerview = view.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        categorylist = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categorylist, getActivity());
        recyclerview.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getLabelsList().observe(getViewLifecycleOwner(), labels -> {
            if (labels != null) {
                categorylist.clear();
                categorylist.addAll(labels); // assuming both are List<String> or similar
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void loadCategory() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MarathiQuotes DB");
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String category = dataSnapshot.getKey();
                    categorylist.add(category);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}