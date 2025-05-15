package com.example.holamundi
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ComentarioDao {
    @Query("SELECT * FROM comentarios WHERE empresaId = :empresaId")
    fun obtenerComentariosPorEmpresa(empresaId: Int): List<ComentarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarComentario(comentario: ComentarioEntity)
}
