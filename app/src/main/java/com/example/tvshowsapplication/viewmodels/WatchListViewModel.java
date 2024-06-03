package com.example.tvshowsapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.tvshowsapplication.database.TVShowsDatabase;
import com.example.tvshowsapplication.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchListViewModel extends AndroidViewModel {

    private TVShowsDatabase tvShowsDatabase;

    public WatchListViewModel(@NonNull Application application) {
        super(application);
        this.tvShowsDatabase = TVShowsDatabase.getTVShowsDatabase(application);
    }

    public Flowable<List<TVShow>> loadWatchList() {
        return tvShowsDatabase.tvShowDao().getWatchList();
    }

    public Completable removeTVShowFromList(TVShow tvShow) {
        return tvShowsDatabase.tvShowDao().removeFromWatchList(tvShow);
    }

}
