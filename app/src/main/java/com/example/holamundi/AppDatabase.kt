package com.example.holamundi

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Usuario::class, EmpresaEntity::class, ViajeEntity::class, ViajeEntity1::class, ComentarioEntity::class ], version = 8)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun empresaDao(): EmpresaDao
    abstract fun viajeDao(): ViajeDao
    abstract fun viajeDao1(): ViajeDao1  // Cambiado aquí
    abstract fun comentarioDao(): ComentarioDao
}

