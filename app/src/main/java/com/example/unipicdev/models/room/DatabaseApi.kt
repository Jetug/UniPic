package com.example.unipicdev.models.room

import com.example.unipicdev.appContext
import com.example.unipicdev.models.room.entities.Sorting
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File

//fun saveDirSorting(dir: String, sorting: SortingType, order: Order){
//    sortingDao.insertAll(Sorting(dir, sorting.toString(), order.toString()))
//}
//
//fun saveDirSorting(dir: File, sorting: SortingType, order: Order){
//    saveDirSorting(dir.absolutePath, sorting, order)
//}
//
//fun getDirSorting(directory: String): Pair<SortingType, Order>{
//    var dir = ""
//    var sorting: String? = null
//    var order: String? = null
//    val dbOpenHelper = SQLite(appContext)
//    val db = dbOpenHelper.readableDatabase
//
//    var test = sortingDao.getAll()
//
//    if(sorting == null || sorting == "")
//        sorting = SortingType.NONE.toString()
//    if(order == null || order == "")
//        order = Order.NONE.toString()
//
//    return SortingType.valueOf(sorting) to Order.valueOf(order)
//}
//
//fun getDirSorting(directory: File): Pair<SortingType, Order> {
//    return getDirSorting(directory.absolutePath)
//}