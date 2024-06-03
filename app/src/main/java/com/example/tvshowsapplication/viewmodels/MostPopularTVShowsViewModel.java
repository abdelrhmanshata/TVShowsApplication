package com.example.tvshowsapplication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvshowsapplication.repositories.MostPopularTVShowRepository;
import com.example.tvshowsapplication.responses.TVShowResponse;

public class MostPopularTVShowsViewModel extends ViewModel  {

    private MostPopularTVShowRepository mostPopularTVShowRepository;

    public MostPopularTVShowsViewModel() {
        mostPopularTVShowRepository = new MostPopularTVShowRepository();
    }

    public LiveData<TVShowResponse> getMostPopularTVShow(int page) {
        return mostPopularTVShowRepository.getMostPopularTVShow(page);
    }
}
