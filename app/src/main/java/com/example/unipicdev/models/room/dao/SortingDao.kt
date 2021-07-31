package com.example.unipicdev.models.room.dao

import androidx.room.*
import com.example.unipicdev.models.room.entities.Sorting
import com.example.unipicdev.models.room.*

@Dao
interface SortingDao {
    @Query(selectALLQuery)
    suspend fun getAll(): List<Sorting>

    @Query(selectALLQuery)
    suspend fun getById(): List<Sorting>

    @Query("SELECT * FROM sorting WHERE dir LIKE :directory LIMIT 1")
    suspend fun getByDir(directory: String): List<Sorting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sorting: Sorting)

    @Delete
    suspend fun delete(sorting: Sorting)
}