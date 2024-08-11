package com.example.mypmt.pmtData

data class BusRoute(
    val number: String="",      // bus number
    val busStops: MutableList<String>?= mutableListOf()
)
