package com.example.holamundi


import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "comentarios")
data class ComentarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val empresaId: Int,
    val texto: String
)