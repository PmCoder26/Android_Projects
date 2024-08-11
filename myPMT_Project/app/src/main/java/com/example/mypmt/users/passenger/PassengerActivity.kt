package com.example.mypmt.users.passenger

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypmt.Canvas
import com.example.mypmt.UserMenuScreen
import com.example.mypmt.navCon
import com.example.mypmt.users.passenger.ui.theme.MyPMTTheme

lateinit var passenNavCon: NavHostController
lateinit var activity: Activity

class PassengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            activity=this
            passenNavCon=rememberNavController()
            MyPMTTheme {
                NavHost(navController = passenNavCon, startDestination = "HomeScreen"){
                    composable("HomeScreen"){
                        MyContent()
                    }
                    composable("AccountScreen"){
                        AccountScreen()
                    }
                    composable("TicketsScreen"){
                        TicketsScreen()
                    }
                    composable("BusesScreen"){
                        BusesScreen()
                    }
                    composable("AccountScreen"){
                        AccountScreen()
                    }
                    composable("PaymentScreen"){
                        PaymentScreen()
                    }
                    composable("UserMenuScreen"){
                        UserMenuScreen()
                    }
                    composable("TicketBookedScreen"){
                        TicketBookedScreen()
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MyContent(){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            MenuScreen()
        }

    }
}

