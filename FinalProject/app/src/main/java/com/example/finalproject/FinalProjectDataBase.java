package com.example.finalproject;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    @Database(entities = {User.class}, version = 1)
    public abstract class FinalProjectDataBase extends RoomDatabase {

        private static final int NUMBER_OF_THREADS = 4;

        public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        public abstract UserDao userDao();

        private static volatile FinalProjectDataBase INSTANCE;

        public static FinalProjectDataBase getdatabase(final Context context) {
            if (INSTANCE == null) {
                synchronized (FinalProjectDataBase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FinalProjectDataBase.class, "MyDB").fallbackToDestructiveMigration().build();
                    }
                }
            }
            return INSTANCE;
        }
    }

