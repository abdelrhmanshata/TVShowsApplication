package com.example.tvshowsapplication.listeners;

import com.example.tvshowsapplication.models.TVShow;

public interface WatchlistListener {

    void onTVShowClicked(TVShow tvShow);

    void removeTvShowFromWatchlist(TVShow tvShow, int position);
}
