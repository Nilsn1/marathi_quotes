package com.nilscreation.marathiquotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nilscreation.marathiquotes.service.AdConfig;
import com.nilscreation.marathiquotes.service.AppConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    ImageView applogo;
    TextView appname, subtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        applogo = findViewById(R.id.applogo);
        appname = findViewById(R.id.appname);
        subtext = findViewById(R.id.subtext);

        applogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        appname.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        subtext.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        fetchAdStatus();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }


    private void fetchAdStatus() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://raw.githubusercontent.com/Nilsn1/nilscreation/main/").addConverterFactory(GsonConverterFactory.create()).build();

        BloggerApiService api = retrofit.create(BloggerApiService.class);
        Call<AdConfig> call = api.getAdConfig();

        call.enqueue(new Callback<AdConfig>() {
            @Override
            public void onResponse(Call<AdConfig> call, Response<AdConfig> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String adStatus = response.body().getAdStatus();
                    AppConfig.getInstance().setShowAds("1".equals(adStatus));
                    AppConfig.getInstance().setNativeAdInterval(response.body().getNativeAdInterval());
                    AppConfig.getInstance().setInterstitialAdInterval(response.body().getInterstitialAdInterval());
                }
            }

            @Override
            public void onFailure(Call<AdConfig> call, Throwable t) {
                AppConfig.getInstance().setShowAds(false); // Default to not showing ads
                Log.d("TAG", "onFailure: " + t.getMessage());

            }
        });
    }
}