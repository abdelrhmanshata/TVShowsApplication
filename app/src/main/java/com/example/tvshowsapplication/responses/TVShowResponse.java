package com.example.tvshowsapplication.responses;

import com.example.tvshowsapplication.models.TVShow;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("pages")
    private int totalPages;
    @SerializedName("tv_shows")
    private List<TVShow> tv_shows;

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<TVShow> getTvShows() {
        return tv_shows;
    }
}
