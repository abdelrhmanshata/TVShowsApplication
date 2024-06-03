package com.example.tvshowsapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.tvshowsapplication.R;
import com.example.tvshowsapplication.adapters.WatchlistAdapter;
import com.example.tvshowsapplication.databinding.ActivityWatchListBinding;
import com.example.tvshowsapplication.listeners.WatchlistListener;
import com.example.tvshowsapplication.models.TVShow;
import com.example.tvshowsapplication.utilities.TempDataHolder;
import com.example.tvshowsapplication.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchListBinding activityWatchListBinding;
    private WatchListViewModel viewModel;

    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchListBinding = DataBindingUtil.setContentView(this, R.layout.activity_watch_list);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        activityWatchListBinding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
        watchlist = new ArrayList<>();
        loadWatchList();
    }

    private void loadWatchList() {
        activityWatchListBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchList().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(tvshow -> {
            activityWatchListBinding.setIsLoading(false);

            if (watchlist.size() > 0) {
                watchlist.clear();
            }
            watchlist.addAll(tvshow);
            watchlistAdapter = new WatchlistAdapter(watchlist, this);
            activityWatchListBinding.watchListRecyclerView.setAdapter(watchlistAdapter);
            activityWatchListBinding.watchListRecyclerView.setVisibility(View.VISIBLE);
            compositeDisposable.dispose();
        }));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHolder.IS_WATCHLIST_UPDATE) {
            loadWatchList();
            TempDataHolder.IS_WATCHLIST_UPDATE = false;
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("TVShow", tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTvShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.removeTVShowFromList(tvShow).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
            watchlist.remove(position);
            watchlistAdapter.notifyItemRemoved(position);
            watchlistAdapter.notifyItemRangeChanged(position, watchlistAdapter.getItemCount());
            compositeDisposable.dispose();
        }));
    }
}