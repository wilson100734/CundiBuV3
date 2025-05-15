package com.example.holamundi



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val nombre: String,  // Será la clave primaria (única)
    val contrasena: String           // Campo de contraseña
)
