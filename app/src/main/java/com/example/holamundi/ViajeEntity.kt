package com.example.holamundi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viajes")
data class ViajeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val origen: String?,
    val destino: String?,
    val tiempoEstimado: String?,
    val distancia: String?,
    val tarifa: String?
)
