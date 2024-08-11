package com.example.mypmt.users.driver

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.example.mypmt.MainActivity
import com.example.mypmt.R
import com.example.mypmt.Spacer
import com.example.mypmt.isPureNumber
import com.example.mypmt.pmtData.Bus
import com.example.mypmt.sharedPref
import com.example.mypmt.toast
import com.example.mypmt.users.passenger.Divider
import com.example.mypmt.users.passenger.passenNavCon
import kotlinx.coroutines.delay

private val driverViewModel by lazy{
    DriverViewModel(activity)
}

@Composable
fun Header(text: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xDC608ADF))
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(text,
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
        )
    }
}

fun toast(text: String) {
    Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show();
}

@Preview(showBackground = true)
@Composable
fun HomeScreen(){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Header("myPMT")
        },
        bottomBar = {
            BottomBar()
        }
    ){
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            MiddleBar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MiddleBar(){
    val isValidBusNo by remember{
        mutableStateOf(driverViewModel.isThereRoute)
    }
    var busNo by remember{
        mutableStateOf("")
    }
    var showRoute by remember{
        mutableStateOf(false)
    }

    if(busNo.length!=3 || !isValidBusNo.value){
        showRoute=false
        isValidBusNo.value=false
    }
    if(isValidBusNo.value){
        showRoute=true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.bus_route),
            contentDescription = "bus route",
            modifier = Modifier.size(150.dp)
        )

        Spacer(20.dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 30.dp, end = 80.dp),
                colors = CardDefaults.elevatedCardColors(Color.White),
                elevation = CardDefaults.elevatedCardElevation(5.dp)
            ) {
                OutlinedTextField(
                    value = busNo,
                    onValueChange = { value ->
                        busNo = value
                    },
                    placeholder = {
                        Text("Enter bus number", fontSize = 18.sp)
                    },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
            Spacer(10.dp)

            ExtendedFloatingActionButton(
                onClick = {
                    if(busNo.isBlank() || busNo.length!=3 || !isPureNumber(busNo)){
                        toast("Invalid bus number!")

                    }
                    else{
                        driverViewModel.getRoute(busNo)
                    }
                },
                shape = RoundedCornerShape(17.dp),
                containerColor =  Color.White,
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                modifier = Modifier.padding(start = 30.dp, top=10.dp)
            ) {
                Text("Get Route",
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                )
            }
        }

        Spacer(10.dp)

        AnimatedVisibility(showRoute && isValidBusNo.value){   // isValidBusNo && showRoute
            RouteCompose()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteCompose(){
    val route = driverViewModel.tempBus.value.busStops
    var isReversed by remember{
        mutableStateOf(false)
    }
    var fromSpot by remember{
        mutableStateOf(route?.get(0) ?: "")
    }
    var toSpot by remember{
        mutableStateOf(route?.get(route?.size?.minus(1) ?: 0) ?: "")
    }

    var busCode by remember{
        mutableStateOf("")
    }
    var temp by remember{
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
        ){
        Text("Route",
            modifier = Modifier.padding(start = 20.dp),
            style = TextStyle(
                fontSize=25.sp,
                fontWeight = FontWeight.Light
            )
            )
        Spacer(10.dp)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 20.dp, end = 20.dp),
            colors = CardDefaults.elevatedCardColors(Color.White),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ){
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround,
                ){
                    Text("From",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        )
                    Text(fromSpot,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                }

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround,
                ){
                    Text("To",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        )
                    Text(toSpot, fontSize = 16.sp)
                }
            }
        }

        Spacer(20.dp)
            ExtendedFloatingActionButton(
                onClick = {
                    isReversed=!isReversed
                    val temp=fromSpot
                    fromSpot=toSpot
                    toSpot=temp
                },
                shape = RoundedCornerShape(17.dp),
                containerColor =  Color.White,
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    "Reverse Route",
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                )
            }

        Spacer(10.dp)

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 30.dp, end = 80.dp),
            colors = CardDefaults.elevatedCardColors(Color.White),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ) {
            OutlinedTextField(
                value = busCode,
                onValueChange = { value ->
                    busCode = value
                },
                placeholder = {
                    Text("Enter bus code", fontSize = 18.sp)
                },
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontSize = 18.sp
                )
            )
        }
        Spacer(10.dp)

        ExtendedFloatingActionButton(
            onClick = {
                if(busCode.isBlank() || busCode.length!=6){
                    toast("Invalid bus code!")
                }
                else{
                    driverViewModel.setBusRoute(busCode, !isReversed)
                }
            },
            shape = RoundedCornerShape(17.dp),
            containerColor =  Color.White,
            elevation = FloatingActionButtonDefaults.elevation(10.dp),
            modifier = Modifier.padding(start=20.dp)
        ) {
            Text("Start",
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBar(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
            }
            .height(75.dp)
            .background(Color(0x59B7C5E7)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(6.dp)
            IconButton(
                onClick = {
                    val options = NavOptions.Builder().setPopUpTo(
                        driverNavCon.graph.startDestinationId, inclusive = true
                    ).build()
                    driverNavCon.navigate("HomeScreen", options)
                },
                modifier = Modifier.size(45.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home), "Home",
                )
            }
            Spacer(2.dp)
            Text("Home")
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    driverNavCon.navigate("AccountScreen")
                },
                modifier = Modifier.size(55.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user), "Account",
                    modifier = Modifier.size(45.dp)
                )
            }
            Text("Account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreen(){
    val driver by driverViewModel.driver
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xD210369C), Color(0xB55A69D8))
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.driver),
                contentDescription = "user information",
                modifier = Modifier.size(150.dp)
            )

            Spacer(10.dp)

            Text(driver.firstName + " "  + driver.lastName,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light
                )
            )

            IconButton(
                onClick={
                    try {
                        driverNavCon.navigate("UserMenuScreen")
                        toast("Signed out successfully!")
                        if(sharedPref.getString("user", "")=="Driver"){
                            try{
                                sharedPref.edit().remove("user")
                                    .remove("driverCode").apply()
                            }
                            catch(e:Exception){
                                toast(e.message.toString())
                            }
                        }
                        activity.startActivity(Intent(
                            activity.applicationContext, MainActivity::class.java
                        ))
                        activity.finish()
                    }
                    catch(e: Exception){
                        toast(e.message.toString())
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 5.dp)
                    .align(Alignment.End)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_logout_24),
                    contentDescription = "logout", tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(30.dp)

        InfoCard("First Name", R.drawable.peron_icon, driver.firstName)
        Spacer(10.dp)
        InfoCard("Last Name", R.drawable.peron_icon, driver.lastName)
        Spacer(10.dp)
        InfoCard("Phone Number", R.drawable.phone_icon, driver.phoneNumber)
        Spacer(10.dp)
        InfoCard("Driver Code", R.drawable.driver_code, driver.driverCode)
    }
}

@Composable
fun InfoCard(title: String, iconResId: Int, data: String){
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title,
            modifier = Modifier.padding(start=35.dp),
            style = TextStyle(
                color=Color.Black,
                fontSize = 18.sp,
                fontWeight= FontWeight.Light
            )
        )

        Spacer(10.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 30.dp, end = 80.dp),
                colors = CardDefaults.elevatedCardColors(Color.White),
                elevation = CardDefaults.elevatedCardElevation(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(20.dp))
                    Icon(painter  = painterResource(iconResId), title)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))
                    Divider()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        data,
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        )
                    )
                }
//                IconButton(
//                    onClick = {
//
//                    },
//                    modifier = Modifier.size(50.dp)
//                ){
//                    Icon(Icons.Default.Edit, "edit data")
//                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteScreen(){
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        val route = driverViewModel.tempBus.value.busStops!!
        val time = driverViewModel.tempBus.value.currentTime!!
        Header("Route")

        Spacer(20.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(route) { destination ->
                    ElevatedCard(
                        modifier = Modifier.size(width=250.dp, height=100.dp),
                        colors = CardDefaults.elevatedCardColors(Color.White),
                        elevation = CardDefaults.elevatedCardElevation(5.dp),
                    ){
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    "Destination : ",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Light,
                                    )
                                )
                                Text( destination,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    )
                            }

                            Spacer(10.dp)

                            if(route.isNotEmpty() && route.indexOf(destination)==0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Text(
                                        "Time : ",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Light,
                                        )
                                    )
                                    Text(
                                        time,
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(10.dp)
                }
            }
            ExtendedFloatingActionButton(
                onClick = {
                          driverViewModel.updateBusRoute()
                },
                modifier = Modifier.align(Alignment.Top),
                containerColor = Color(0xFF608ADF),
                elevation = FloatingActionButtonDefaults.elevation(10.dp)
            ) {
                Text("Finish",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                    )
            }
        }

    }
}