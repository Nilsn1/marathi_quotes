package com.nilscreation.marathiquotes;


import com.nilscreation.marathiquotes.model.BloggerModel;
import com.nilscreation.marathiquotes.service.AdConfig;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BloggerApiService {
    @GET("blogs/3174392454774953694/posts/4298350139184762452?key=AIzaSyDcrsQAbkV8ZFPslaklDJGG6lroV2UCE4M")
    Call<BloggerModel> getBlogPosts();

    @GET("blogs/7575014297641844201/posts/9122830914565179?key=AIzaSyDcrsQAbkV8ZFPslaklDJGG6lroV2UCE4M")
    Call<BloggerModel> getImages();

    @GET("marathi_quotes_config.json")
    Call<AdConfig> getAdConfig();

}

