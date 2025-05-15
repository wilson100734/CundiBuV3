package com.example.holamundi

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyId: Int,
    val rating: Float
)
