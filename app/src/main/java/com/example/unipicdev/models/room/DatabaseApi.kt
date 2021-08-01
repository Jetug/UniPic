package com.example.unipicdev.models.room

import android.util.Log
import com.example.unipicdev.models.room.entities.Sorting
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File
import kotlin.system.measureTimeMillis

const val dbVersion = 1
const val dbName = "UniPicDB"
const val sortingTableName = "Sorting"
const val dirField = "dir"
const val orderField = "sortingOrder"
const val sortingField = "sorting"
const val creationString = "create table $sortingTableName ( _id integer primary key autoincrement, $dirField TEXT, $sortingField TEXT, $orderField TEXT)"
const val selectALLQuery = "SELECT * FROM $sortingTableName"

private var sortingList: MutableList<Sorting> = mutableListOf()

fun getAllSorting(): List<Sorting>{
    return initSortingList()
}

suspend fun saveMediaSorting(dir: String, sortingType: SortingType, order: Order){
    val sorting = Sorting(dir, sortingType.toString(), order.toString())
    initSortingList()
    insertSorting(sorting)
    sortingDao.insert(sorting)
}

suspend fun saveMediaSorting(dir: File, sortingType: SortingType, order: Order){
    saveMediaSorting(dir.absolutePath, sortingType, order)
}

fun getMediaSorting(directory: String): Pair<SortingType, Order>{
    val pair: Pair<SortingType, Order>
    val time = measureTimeMillis {
        initSortingList()
        val sorting = getSortingByDir(directory)

        if (sorting == null)
            pair = SortingType.NONE to Order.NONE
        else
            pair = SortingType.valueOf(sorting.sortingType) to
                    Order.valueOf(sorting.sortingOrder)
    }
    Log.d("My", "getMediaSorting $time ms")
    return pair

    //val sortingList = sortingDao.getByDir(directory)
//    return if(sortingList.isEmpty())
//        SortingType.NONE to Order.NONE
//    else{
//        val sorting = sortingList[0]
//        SortingType.valueOf(sorting.sortingType) to
//                Order.valueOf(sorting.sortingOrder)
//    }
}

suspend fun getMediaSorting(directory: File): Pair<SortingType, Order> {
    return getMediaSorting(directory.absolutePath)
}

private fun insertSorting(sorting: Sorting){
    for(i in 0 until sortingList.size){
        if(sortingList[i].dir == sorting.dir){
            sortingList[i] = sorting
            return
        }
    }
    sortingList.add(sorting)
}

private fun getSortingByDir(dir: String): Sorting? {
    sortingList.forEach{
        if(it.dir == dir) return it
    }
    return null
}

private fun initSortingList(): List<Sorting>{
    if (sortingList.isEmpty()){
        sortingList = sortingDao.getAll().toMutableList()
    }
    return sortingList
}