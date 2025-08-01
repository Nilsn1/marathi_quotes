package com.nilscreation.marathiquotes.service;

import com.google.gson.annotations.SerializedName;

public class AdConfig {
    public AdConfig() {
        // Required empty constructor for Gson
    }

    @SerializedName("ad_status")
    private String adStatus;
    @SerializedName("native_ad_interval")
    private int nativeAdInterval;
    @SerializedName("interstitial_ad_interval")
    private int interstitialAdInterval;

    public String getAdStatus() {
        return adStatus;
    }

    public int getNativeAdInterval() {
        return nativeAdInterval;
    }

    public int getInterstitialAdInterval() {
        return interstitialAdInterval;
    }
}
