package com.example.mypmt

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypmt.ui.theme.MyPMTTheme
import com.example.mypmt.users.driver.DriverActivity
import com.example.mypmt.users.driver.DriverLoginClient
import com.example.mypmt.users.passenger.PassengerActivity
import com.example.mypmt.users.passenger.PassengerLogInClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

lateinit var navCon: NavHostController
lateinit var passenLoginClient: PassengerLogInClient
lateinit var driverLoginClient: DriverLoginClient
lateinit var activity: Activity
 lateinit var sharedPref: SharedPreferences
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPMTTheme {
                activity=this
                navCon = rememberNavController()
                sharedPref=getSharedPreferences("userCred", MODE_PRIVATE)
                passenLoginClient= PassengerLogInClient(this)
                driverLoginClient=DriverLoginClient(this)
                NavHost(navController = navCon, startDestination = "Main") {
                    composable("Main") {
                        MyContent()
                    }
                    composable("UserMenuScreen") {
                        UserMenuScreen()
                    }
                    composable("PassengerLogin"){
                        PassengerSignIn()
                    }
                    composable("DriverLogin"){
                        DriverLogin()
                    }
                    composable("PassengerSignUp"){
                        PassengerSignUp()
                    }
                    composable("OtpScreen"){
                        OtpScreen()
                    }
                }
            }
        }
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Preview(showBackground = true)
    @Composable
    fun MyContent() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isLoggedIn by remember {
                mutableStateOf(false)
            }
            runBlocking {
                launch {
                    try {
                        if (sharedPref.contains("user") ) {
                            if(sharedPref.getString("user", "")?.isNotBlank() == true) {
                                isLoggedIn = true
                            }
                        } else {
                            isLoggedIn = false
                        }
                    }
                    catch(e:Exception){
                        toast(e.message.toString())
                    }
                }
            }

            if(isLoggedIn){
                try {
                    val user = sharedPref.getString("user", "");
                    if (user == "passenger") {
                        val i = Intent(this@MainActivity, PassengerActivity::class.java)
                        finish()
                        startActivity(i)
                    }
                    else if(user == "Driver"){
                        val i = Intent(this@MainActivity, DriverActivity::class.java)
                        finish()
                        startActivity(i)
                    }
                    else {
                        toast("Login error!")
                    }
                }
                catch(e: Exception){
                    toast(e.message.toString())
                }
            }
            else{
                navCon.navigate("UserMenuScreen")
            }

        }


    }

}

