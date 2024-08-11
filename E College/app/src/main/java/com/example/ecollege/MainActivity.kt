package com.example.ecollege

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ecollege.authentication.PhoneAuthenticationViewModel
import com.example.ecollege.routes_package.Route
import com.example.ecollege.ui.theme.ECollegeTheme

class MainActivity : ComponentActivity() {

    private val viewModel by lazy{
        PhoneAuthenticationViewModel(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECollegeTheme {
                MyContent()
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
            if(viewModel.currentUser == null){
                var phoneNumber by remember{
                    mutableStateOf("")
                }
                var otp by remember{
                    mutableStateOf("")
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { value ->
                        phoneNumber = value
                    },
                    placeholder = {
                        Text("Phone number")
                    }
                )
                OutlinedTextField(
                    value = otp,
                    onValueChange = { value ->
                        otp = value
                    },
                    placeholder = {
                        Text("OTP")
                    }
                )

                Button(
                    onClick = {
                        viewModel.sendVerificationCode(phoneNumber)
                    },
                ) {
                    Text("Send OTP")
                }

                Button(
                    onClick = {
                        viewModel.verifyCodeAndSignIn(otp)
                    },
                ) {
                    Text("Submit OTP")
                }


            }
        }
    }

}
