package com.example.dibujarlotes.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class TipoArea extends MasterTable{
    @PrimaryKey
    @NonNull
    private UUID Uuid;

    private String Nombre;

    private String Descripcion;

    public TipoArea() {
        this.Uuid = UUID.randomUUID();
        this.CreatedDate = new Date();
    }

    public TipoArea(@NonNull UUID uuid, String nombre, String descripcion) {
        Uuid = uuid;
        Nombre = nombre;
        Descripcion = descripcion;
    }

    @NonNull
    public UUID getUuid() {
        return Uuid;
    }

    public void setUuid(@NonNull UUID uuid) {
        Uuid = uuid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
}
