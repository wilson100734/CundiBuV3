package com.example.holamundi
import androidx.room.Entity
import androidx.room.PrimaryKey
// Después:
@Entity(tableName = "viajes1")
data class ViajeEntity1(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val origen: String?,
    val destino: String?,

    val horaSalida: String? // Asegúrate de incluir este campo
)
