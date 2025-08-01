package com.nilscreation.marathiquotes.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<String>> labelsList = new MutableLiveData<>();

    public LiveData<List<String>> getLabelsList() {
        return labelsList;
    }

    public void setLabelsList(List<String> labels) {
        labelsList.setValue(labels);
    }
}
