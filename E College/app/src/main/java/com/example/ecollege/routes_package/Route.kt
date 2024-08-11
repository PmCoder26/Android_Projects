package com.example.ecollege.routes_package

import android.app.Activity

data class Route(
    val text: String,
    val destination: Class<out Activity>
)
