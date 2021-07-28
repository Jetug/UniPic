package com.example.unipicdev.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.unipicdev.appContext
import com.example.unipicdev.views.adapters.SortingType
import java.io.File


private const val dbVersion = 1
private const val dbName = "UniPic"
private const val sortingTableName = "dirSorting"
private const val dirField = "dir"
private const val sortingField = "sorting"
private const val creationString = "create table $sortingTableName ( id integer primary key autoincrement, $dirField TEXT, $sortingField TEXT)"
private const val selectALLQuery = "SELECT * FROM $sortingTableName"

val dbOpenHelper = SQLite(appContext)

class SQLite(context:Context): SQLiteOpenHelper(context, dbName,null, dbVersion) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(creationString)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}

fun saveDirSorting(dir: String, sorting: SortingType){
    val db: SQLiteDatabase = dbOpenHelper.writableDatabase
    val cv = ContentValues()
    cv.put(dirField, dir)
    cv.put(sortingField, sorting.toString())
    db.insert(sortingTableName, null, cv)
    db.close()
}

fun saveDirSorting(dir: File, sorting: SortingType){
    saveDirSorting(dir.absolutePath, sorting)
}

fun getDirSorting(directory: String): SortingType {
    var dir: String = ""
    var sorting: String? = null
    val db = dbOpenHelper.readableDatabase

    val cursor = db.rawQuery(selectALLQuery, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            do {
                dir = cursor.getString(cursor.getColumnIndex(dirField))
                if(dir == directory) {
                    sorting = cursor.getString(cursor.getColumnIndex(sortingField))
                    break
                }
            } while (cursor.moveToNext())
        }
    }
    cursor.close()
    db.close()

    if(sorting == null)
        return SortingType.NONE
    return SortingType.valueOf(sorting)
}

fun getDirSorting(directory: File): SortingType {
    return getDirSorting(directory.absolutePath)
}