package com.example.unipicdev.models.room

import androidx.room.*
import com.example.unipicdev.appContext
import com.example.unipicdev.models.room.dao.SortingDao
import com.example.unipicdev.models.room.entities.Sorting

@Database(entities = arrayOf(Sorting::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sortingDao(): SortingDao
}

val database = Room.databaseBuilder(appContext, AppDatabase::class.java, dbName+"2").allowMainThreadQueries().build()
val sortingDao = database.sortingDao()