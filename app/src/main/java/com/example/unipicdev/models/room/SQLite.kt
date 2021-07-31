package com.example.unipicdev.models.room

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.unipicdev.appContext
import com.example.unipicdev.models.room.entities.Sorting
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File


const val dbVersion = 1
const val dbName = "UniPicDB"
const val sortingTableName = "Sorting"
const val dirField = "dir"
const val orderField = "sortingOrder"
const val sortingField = "sorting"
const val creationString = "create table $sortingTableName ( _id integer primary key autoincrement, $dirField TEXT, $sortingField TEXT, $orderField TEXT)"
const val selectALLQuery = "SELECT * FROM $sortingTableName"



class SQLite(context:Context): SQLiteOpenHelper(context, dbName,null, dbVersion) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(creationString)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}

//fun saveDirSorting(dir: String, sorting: SortingType, order: Order){
//    val dbOpenHelper = SQLite(appContext)
//    val db: SQLiteDatabase = dbOpenHelper.writableDatabase
//    val cv = ContentValues()
//    cv.put(dirField, dir)
//    cv.put(sortingField, sorting.toString())
//    cv.put(orderField, order.toString())
//    db.insert(sortingTableName, null, cv)
//    db.close()
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
//    val cursor = db.rawQuery(selectALLQuery, null)
//    if (cursor != null) {
//        if (cursor.moveToFirst()) {
//            do {
//                dir = cursor.getString(cursor.getColumnIndex(dirField))
//                if(dir == directory) {
//                    sorting = cursor.getString(cursor.getColumnIndex(sortingField))
//                    break
//                }
//            } while (cursor.moveToNext())
//        }
//    }
//    cursor.close()
//    db.close()
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

suspend fun saveDirSorting(dir: String, sorting: SortingType, order: Order){
    sortingDao.insert(Sorting(dir, sorting.toString(), order.toString()))
}

suspend fun saveDirSorting(dir: File, sorting: SortingType, order: Order){
    saveDirSorting(dir.absolutePath, sorting, order)
}

suspend fun getDirSorting(directory: String): Pair<SortingType, Order>{
    val sortingList = sortingDao.getByDir(directory)

    return if(sortingList.isEmpty())
        SortingType.NONE to Order.NONE
    else{
        val sorting = sortingList[0]
        SortingType.valueOf(sorting.sortingType) to
                Order.valueOf(sorting.sortingOrder)
    }
}

suspend fun getDirSorting(directory: File): Pair<SortingType, Order> {
    return getDirSorting(directory.absolutePath)
}