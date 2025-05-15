package com.example.holamundi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ViajeDao1 {
    @Insert
    fun insertarViaje(viaje: ViajeEntity1)

    @Query("SELECT * FROM viajes1")
    fun obtenerTodos(): List<ViajeEntity1>
}