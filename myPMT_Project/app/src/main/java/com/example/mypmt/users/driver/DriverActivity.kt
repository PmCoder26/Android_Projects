package com.example.mypmt.users.driver

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypmt.UserMenuScreen
import com.example.mypmt.users.driver.ui.theme.MyPMTTheme


lateinit var activity: Activity
lateinit var driverNavCon: NavHostController

class DriverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            activity=this
            driverNavCon= rememberNavController()
            MyPMTTheme {
                NavHost(navController = driverNavCon, startDestination = "HomeScreen"){
                    composable("HomeScreen"){
                        HomeScreen()
                    }
                    composable("AccountScreen"){
                        AccountScreen()
                    }
                    composable("UserMenuScreen"){
                        UserMenuScreen()
                    }
                    composable("RouteScreen"){
                        RouteScreen()
                    }
                }
            }
        }
    }


}

