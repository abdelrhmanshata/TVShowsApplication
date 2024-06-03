package com.example.tvshowsapplication.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tvshowsapplication.network.ApiClient;
import com.example.tvshowsapplication.network.ApiService;
import com.example.tvshowsapplication.responses.TVShowResponse;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Response;

public class MostPopularTVShowRepository {

    private ApiService apiService;

    public MostPopularTVShowRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowResponse> getMostPopularTVShow(int page) {
        MutableLiveData<TVShowResponse> data = new MutableLiveData<>();
        apiService.getMostPopularTVShow(page).enqueue(new retrofit2.Callback<TVShowResponse>() {
            @Override
            public void onResponse(Call<TVShowResponse> call, @NotNull Response<TVShowResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<TVShowResponse> call, @NotNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
