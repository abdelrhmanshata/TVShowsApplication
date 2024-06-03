package com.example.tvshowsapplication.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tvshowsapplication.R;
import com.example.tvshowsapplication.adapters.EpisodesAdapter;
import com.example.tvshowsapplication.adapters.ImageSliderAdapter;
import com.example.tvshowsapplication.databinding.ActivityTvshowDetailsBinding;
import com.example.tvshowsapplication.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.tvshowsapplication.models.TVShow;
import com.example.tvshowsapplication.utilities.TempDataHolder;
import com.example.tvshowsapplication.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog bottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;

    private Boolean isTVShowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);

        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailsBinding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
        tvShow = (TVShow) getIntent().getSerializableExtra("TVShow");
        getTvShowDetails();
        checkTvShowInWatchList();

    }

    private void checkTvShowInWatchList() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId())).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(tvShow1 -> {
            isTVShowAvailableInWatchlist = true;
            activityTvshowDetailsBinding.imageWatchList.setImageResource(R.mipmap.ic_check);
            compositeDisposable.dispose();
        }));
    }

    @SuppressLint("SetTextI18n")
    private void getTvShowDetails() {
        activityTvshowDetailsBinding.setIsLoading(true);
        tvShowDetailsViewModel.getTVShowDetails(String.valueOf(tvShow.getId())).observe(this, tvShowDetailsResponse -> {
            activityTvshowDetailsBinding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                activityTvshowDetailsBinding.setTvShowImageUrl(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                activityTvshowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.setDescription(String.valueOf(HtmlCompat.fromHtml(tvShowDetailsResponse.getTvShowDetails().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.textReadMore.setOnClickListener(v -> {
                    if (activityTvshowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                        activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                        activityTvshowDetailsBinding.textReadMore.setText(R.string.read_less);
                    } else {
                        activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                        activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        activityTvshowDetailsBinding.textReadMore.setText(R.string.read_more);
                    }
                });

                activityTvshowDetailsBinding.setRating(String.format(Locale.getDefault(), "%.2f", Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())));
                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    activityTvshowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    activityTvshowDetailsBinding.setGenre("N/A");
                }

                activityTvshowDetailsBinding.setRunTime(tvShowDetailsResponse.getTvShowDetails().getRunTime() + " Min");
                activityTvshowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);

                activityTvshowDetailsBinding.buttonWebsite.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                    startActivity(intent);
                });
                activityTvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);

                activityTvshowDetailsBinding.buttonEpisodes.setOnClickListener(v -> {
                    if (bottomSheetDialog == null) {
                        bottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_episodes_bottom_sheet, findViewById(R.id.episodesContainer), false);
                        bottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                        layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes()));
                        layoutEpisodesBottomSheetBinding.textTitle.setText(String.format("Episodes | %s", tvShow.getName()));
                        layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> {
                            bottomSheetDialog.dismiss();
                        });
                    }

                    // --- Optional Section Start --- //
//                    FrameLayout frameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//                    if (frameLayout != null) {
//                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
//                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
//                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    }
                    // --- Optional Section End --- //

                    bottomSheetDialog.show();

                });

                activityTvshowDetailsBinding.imageWatchList.setOnClickListener(view -> {
                    TempDataHolder.IS_WATCHLIST_UPDATE = true;
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if (isTVShowAvailableInWatchlist) {
                        compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            isTVShowAvailableInWatchlist = false;
                            activityTvshowDetailsBinding.imageWatchList.setImageResource(R.mipmap.ic_eye);
                            Toast.makeText(getApplicationContext(), "Removed from watchlist", Toast.LENGTH_SHORT).show();
                            compositeDisposable.dispose();
                        }));
                    } else {
                        compositeDisposable.add(tvShowDetailsViewModel.addToWatchlist(tvShow).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            activityTvshowDetailsBinding.imageWatchList.setImageResource(R.mipmap.ic_check);
                            Toast.makeText(getApplicationContext(), "Added To Watchlist", Toast.LENGTH_SHORT).show();
                            compositeDisposable.dispose();
                        }));
                    }
                });

                activityTvshowDetailsBinding.imageWatchList.setVisibility(View.VISIBLE);
                loadBasicTVShowDetails(tvShow);
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        activityTvshowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvshowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);

        setupSliderIndicators(sliderImages.length);
        activityTvshowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                setCurrentSliderIndicator(position);
            }
        });

    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            }
        }
    }

    private void loadBasicTVShowDetails(TVShow tvShow) {
        activityTvshowDetailsBinding.setTvShowObj(tvShow);
    }


}

