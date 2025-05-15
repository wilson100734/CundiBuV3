package com.example.holamundi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    // Verifica si existe un usuario con ese nombre y contraseña
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena")
    fun verificarUsuario(nombre: String, contrasena: String): Usuario?

    // Inserta un usuario (por ejemplo, uno de prueba)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertarUsuario(usuario: Usuario)
}