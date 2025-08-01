package com.nilscreation.marathiquotes.service;

public class AppConfig {
    private static AppConfig instance;
    private boolean showAds;
    private int nativeAdInterval;
    private int interstitialAdInterval;

    private AppConfig() {
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }

    public void setNativeAdInterval(int nativeAdInterval) {
        this.nativeAdInterval = nativeAdInterval;
    }

    public void setInterstitialAdInterval(int interstitialAdInterval) {
        this.interstitialAdInterval = interstitialAdInterval;
    }

    public int getNativeAdInterval() {
        return nativeAdInterval;
    }

    public int getInterstitialAdInterval() {
        return interstitialAdInterval;
    }
}
