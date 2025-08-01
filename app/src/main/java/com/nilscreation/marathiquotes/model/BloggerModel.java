package com.nilscreation.marathiquotes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BloggerModel {

    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("labels")
    private List<String> labels;

    public BloggerModel() {
    }

    public BloggerModel(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
