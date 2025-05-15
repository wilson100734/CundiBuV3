package com.example.holamundi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmpresaDao {
    @Query("SELECT * FROM empresas")
    fun obtenerEmpresas(): List<EmpresaEntity>

    @Query("SELECT * FROM empresas WHERE id = :id")
    fun obtenerEmpresaPorId(id: Int): EmpresaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarEmpresa(empresa: EmpresaEntity)
}