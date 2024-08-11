package com.example.mypmt

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mypmt.users.driver.Driver
import com.example.mypmt.users.driver.DriverActivity
import com.example.mypmt.users.passenger.PassengerActivity
import com.example.mypmt.users.passenger.Person
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Composable
fun Canvas(){
    val brush = Brush.linearGradient(
        listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
    )
    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw={
            drawRoundRect(brush)
        }
    )
}
@Composable
fun Spacer(value: Dp){
    Spacer(modifier = Modifier.height(value))
}

val user by lazy{
    mutableStateOf("")
}

@Preview(showBackground = true)
@Composable
fun UserMenuScreen(){
    Canvas()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("myPMT",
            style = TextStyle(
                color = Color(0xE2000000),
                fontFamily = FontFamily.Cursive,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
            )
            )

        val movingBusCompo by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.moving_bus)
        )
        val passengerComp by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.passenger)
        )
        val driverComp by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.driver)
        )

        LottieAnimation(
            composition = movingBusCompo,
            renderMode = RenderMode.AUTOMATIC,
            iterations = Integer.MAX_VALUE,
            isPlaying = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.passenger),
                    contentDescription = "passenger img",
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            navCon.navigate("PassengerLogin")
                        }
                        .size(160.dp),
                )
                Spacer(Modifier.height(5.dp))
                Text("Passenger")
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bus_driver),
                    contentDescription = "driver img",
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            navCon.navigate("DriverLogin")
                        }
                        .size(160.dp),
                )
                Spacer(5.dp)
                Text("Driver")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PassengerSignIn(){
    Canvas()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var number by remember{
            mutableStateOf("")
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.otp_verification),
                contentDescription = "OTP verification",
                modifier = Modifier.size(100.dp)
            )

            Spacer(20.dp)

            Text("OTP Verification",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                )

            Spacer(30.dp)

            Text("Verify and sign in using phone number",
                style = TextStyle(
                    fontSize = 18.sp
                )
                )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Enter your phone number to get OTP",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                )

            Spacer(10.dp)

            number = textField(text = "Enter your phone number")

            Spacer(40.dp)

            Button(
                onClick = {
                    if (isPureNumber(number)) {
                        try {
                            user.value = "passenger"
                            passenLoginClient.signIn(number)
                        }
                        catch(e: Exception){
                            toast(e.message.toString())
                        }
                    } else {
                        toast("Invalid phone number!")
                    }
                },
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(Color(0xBE145291))
            ) {
                Text(
                    "Generate OTP",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    )
                )
                Spacer(20.dp)
            }

            Spacer(20.dp)

            Button(
                onClick = {
                    navCon.navigate("PassengerSignUp")
                },
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(Color(0xBE145291))
            ) {
                Text(
                    "Sign up",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PassengerSignUp(){
    var firstName by remember{
        mutableStateOf("")
    }
    var lastName by remember{
        mutableStateOf("")
    }
    var gender by remember{
        mutableStateOf("")
    }
    var phoneNumber by remember{
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){

       Column(
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Image(
               painter = painterResource(R.drawable.passen_info),
               contentDescription = "user information filling",
               modifier = Modifier.size(100.dp)
           )

           Spacer(10.dp)

           Text(
               "Enter your details to sign up!",
               style = TextStyle(
                   fontSize = 22.sp
               )
           )
       }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            firstName = textField(text = "First Name")
            Spacer(10.dp)
            lastName = textField(text = "Last Name")
            Spacer(10.dp)
            gender = textField(text = "Gender")
            Spacer(10.dp)
            phoneNumber = textField(text = "Phone Number")
            Spacer(30.dp)

            Button(
                onClick = {
                    if (isPureWord(firstName) && isPureWord(lastName)
                        && isPureWord(gender) && isPureNumber(phoneNumber)
                    ) {
                        val p = Person(
                            firstName, lastName, gender, phoneNumber, "0"
                        )
                        user.value = "passenger"
                        passenLoginClient.signUp(p)
                        if (passenLoginClient.isOtpSent()) {
                            navCon.navigate("OtpScreen")
                        }
                    } else {
                        toast("Invalid credentials!")
                    }
                },
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(Color(0xBE145291))
            ) {
                Text(
                    "Generate OTP",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DriverLogin(){
    Canvas()

    var firstName by remember{
        mutableStateOf("")
    }
    var lastName by remember{
        mutableStateOf("")
    }
    var driverCode by remember{
        mutableStateOf("")
    }
    var phoneNumber by remember{
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.driver),
                contentDescription = "Driver Login",
                modifier = Modifier.size(100.dp)
            )

            Spacer(10.dp)

            Text("Driver Login",
                style = TextStyle(
                    fontSize = 22.sp
                )
                )

        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            firstName = textField(text = "First Name")
            Spacer(10.dp)
            lastName = textField(text = "Last Name")
            Spacer(10.dp)
            driverCode = textField(text = "Driver Code")
            Spacer(10.dp)
            phoneNumber = textField(text = "Phone Number")
            Spacer(10.dp)

            Button(
                onClick = {
                    if (isPureWord(firstName) && isPureWord(lastName)
                        && isPureNumber(driverCode) && isPureNumber(phoneNumber)
                    ) {
                        val d = Driver(
                            driverCode, firstName, lastName, phoneNumber
                        )
                        user.value = "Driver"
                        driverLoginClient.signIn(d)
                    } else {
                        toast("Invalid credentials!")
                    }
                },
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(Color(0xBE145291))
            ) {
                Text(
                    "Generate OTP",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var otp by remember{
            mutableStateOf("")
        }

        Image(
            painter = painterResource(id = R.drawable.otp),
            contentDescription = "Verify OTP",
            modifier = Modifier.size(100.dp)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            otp = textField(text = "Enter the OTP")

            Spacer(20.dp)

            Button(
                onClick = {
                    if (isPureNumber(otp)) {
                        if (user.value == "passenger") {
                            passenLoginClient.verifyCodeAndSignIn(
                                otp, passenLoginClient.getPassenger()
                            )
                        } else if (user.value == "Driver") {
                            driverLoginClient.verifyCodeAndSignIn(
                                otp, driverLoginClient.getDriver()
                            )
                        }
                    } else {
                        toast("Invalid credentials!")
                    }
                },
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(Color(0xBE145291))
            ) {
                Text(
                    "Continue",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textField(text: String): String{
    var data by remember{
        mutableStateOf("")
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(start = 30.dp, end = 30.dp),
        colors = CardDefaults.elevatedCardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(5.dp)
    ) {
        OutlinedTextField(
            value = data,
            onValueChange = { value ->
                    data = value
            },
            placeholder = {
                Text(text, fontSize = 18.sp)
            },
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(
                fontSize = 18.sp
            )
        )
    }
    return data
}

fun toast(text: String) {
    Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show();
}

fun isPureNumber(text: String): Boolean{
    for(c in text){
        if(c.isLetter()){
            return false
        }
    }
    return true
}
fun isPureWord(text: String): Boolean{
    for(c in text){
        if(c.isDigit()){
            return false
        }
    }
    return true
}