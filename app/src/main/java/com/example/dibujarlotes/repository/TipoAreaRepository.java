package com.example.dibujarlotes.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dibujarlotes.AppDatabase;
import com.example.dibujarlotes.dao.TipoAreaDao;
import com.example.dibujarlotes.entity.TipoArea;

import java.util.List;

public class TipoAreaRepository {
    private TipoAreaDao mTipoAreaDao;
    private LiveData<List<TipoArea>> mAllTipoAreas;

    public TipoAreaRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTipoAreaDao = db.tipoAreaDao();
        mAllTipoAreas = mTipoAreaDao.getObservable();
    }

    public LiveData<List<TipoArea>> getObservable() {
        return mAllTipoAreas;
    }

    public void insert(TipoArea tipoArea) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTipoAreaDao.insert(tipoArea);
        });
    }
}
