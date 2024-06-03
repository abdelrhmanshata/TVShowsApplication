package com.example.tvshowsapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tvshowsapplication.dao.TvShowDao;
import com.example.tvshowsapplication.models.TVShow;

@Database(entities = TVShow.class, version = 1, exportSchema = false)
public abstract class TVShowsDatabase extends RoomDatabase {

    private static TVShowsDatabase tvShowsDatabase;

    public static synchronized TVShowsDatabase getTVShowsDatabase(Context context) {
        if (tvShowsDatabase == null) {
            tvShowsDatabase = Room.databaseBuilder(context, TVShowsDatabase.class, "tv_shows_database").build();
        }
        return tvShowsDatabase;
    }

    public abstract TvShowDao tvShowDao();

}
