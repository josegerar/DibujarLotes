package com.example.dibujarlotes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dibujarlotes.entity.TipoArea;

import java.util.List;

public interface TipoAreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(TipoArea... tipoAreas);

    @Update
    public void update(TipoArea... tipoAreas);

    @Delete
    public void delete(TipoArea... tipoAreas);

    @Query("DELETE FROM TipoArea")
    public void deleteAll();

    @Query("SELECT * FROM TipoArea ORDER BY Nombre ASC")
    public List<TipoArea> get();

    @Query("SELECT * FROM TipoArea ORDER BY Nombre ASC")
    public LiveData<List<TipoArea>> getObservable();
}
