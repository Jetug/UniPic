package com.example.unipicdev.models.room.entities

import androidx.room.*
import com.example.unipicdev.appContext
import com.example.unipicdev.models.room.*
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File

@Entity
data class Sorting (
    @PrimaryKey
    val dir: String,
    val sortingType: String,
    val sortingOrder: String
)