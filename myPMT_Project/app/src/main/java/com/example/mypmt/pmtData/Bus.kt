package com.example.mypmt.pmtData

data class Bus(
    var number: String="",
    var code: String="",
    var busStops: MutableList<String>?= mutableListOf(),
    var currentTime: String="",
)
