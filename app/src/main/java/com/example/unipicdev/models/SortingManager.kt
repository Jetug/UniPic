package com.example.unipicdev.models

import com.example.unipicdev.models.room.getAllSorting
import com.example.unipicdev.views.adapters.*

class SortingManager {
    val sortingMap: MutableMap<String, Pair<SortingType, Order>> = mutableMapOf()

    init{
        getAllSorting()
    }
}