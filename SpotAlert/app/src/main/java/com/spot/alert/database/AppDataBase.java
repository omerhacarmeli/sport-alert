package com.spot.alert.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.dataobjects.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    @Database(entities = {User.class, Location.class, LocationTimeRange.class}, version = 3)
    public abstract class AppDataBase extends RoomDatabase {

        private static final int NUMBER_OF_THREADS = 4;

        public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        public abstract UserDao userDao();

        public abstract LocationDao locationDao();

        private static volatile AppDataBase INSTANCE;

        public static AppDataBase getDatabase(final Context context) {
            if (INSTANCE == null) {
                synchronized (AppDataBase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "SpotAlertDB_1").allowMainThreadQueries().fallbackToDestructiveMigration().build();
                    }
                }
            }
            return INSTANCE;
        }
    }

