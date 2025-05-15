package com.example.holamundi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ViajeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarViaje(viaje: ViajeEntity)

    @Query("SELECT * FROM viajes ORDER BY id DESC")
    fun obtenerTodosLosViajes(): List<ViajeEntity>
}
