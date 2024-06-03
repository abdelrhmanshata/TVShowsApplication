package com.example.tvshowsapplication.network;

import com.example.tvshowsapplication.responses.TVShowDetailsResponse;
import com.example.tvshowsapplication.responses.TVShowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("most-popular")
    Call<TVShowResponse> getMostPopularTVShow(@Query("page") int page);

    @GET("show-details")
    Call<TVShowDetailsResponse> getTVShowDetails(@Query("q") String tvShowID);

    @GET("search")
    Call<TVShowResponse> searchTvShow(@Query("q") String query, @Query("page") int page);


}
