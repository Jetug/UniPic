package com.example.unipicdev

import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType

var mediaSortingType: SortingType = SortingType.NAME
var mediaSortingOrder: Order = Order.ASCENDING

var directorySortingType: SortingType = SortingType.NAME
var directorySortingOrder: Order = Order.ASCENDING

var defaultMediaSortingType: SortingType = SortingType.MODIFICATION_DATE
var defaultMediaSortingOrder: Order = Order.DESCENDING

fun getNotNoneSortingType(sortingType: SortingType) = if(sortingType == SortingType.NONE) defaultMediaSortingType else sortingType
fun getNotNoneSortingOrder(order: Order) = if(order == Order.NONE) defaultMediaSortingOrder else order