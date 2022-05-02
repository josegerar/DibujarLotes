package com.example.dibujarlotes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.dibujarlotes.converter.DateConverter;
import com.example.dibujarlotes.converter.UUIDConverter;
import com.example.dibujarlotes.dao.TipoAreaDao;
import com.example.dibujarlotes.entity.TipoArea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TipoArea.class}, version = 1)
@TypeConverters({DateConverter.class, UUIDConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "CalibradorDatabase";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract TipoAreaDao tipoAreaDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addCallback(sAppDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static AppDatabase.Callback sAppDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                //TipoAreaDao dao = INSTANCE.tipoAreaDao();
                //dao.deleteAll();

                /*TipoArea word = new TipoArea();
                dao.insert(word);
                word = new TipoArea();
                dao.insert(word);*/
            });
        }
    };
}
