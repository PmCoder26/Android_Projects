package com.example.mypmt.users.passenger

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Email
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavOptions
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mypmt.Canvas
import com.example.mypmt.MainActivity
import com.example.mypmt.R
import com.example.mypmt.Spacer
import com.example.mypmt.isPureNumber
import com.example.mypmt.isPureWord
import com.example.mypmt.navCon
import com.example.mypmt.passenLoginClient
import com.example.mypmt.pmtData.Bus
import com.example.mypmt.pmtData.Route
import com.example.mypmt.pmtData.Ticket
import com.example.mypmt.sharedPref
import com.example.mypmt.textField
import com.example.mypmt.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val passenViewModel by lazy{
    PassengerViewModel(activity)
}
private val tempRoute by lazy{
    mutableStateOf(Route())
}

@Composable
fun Divider(){
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .padding(top = 10.dp, bottom = 10.dp)
    )
}
@Composable
fun toast(text: String){
    Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show()
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


@Preview(showBackground = true)
@Composable
fun MenuScreen(){
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar()
        }
    ){
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            MiddleBar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBar(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
            }
            .height(60.dp)
            .background(Color(0x7E608ADF))
        ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text("myPMT",
            style = TextStyle(
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Cursive,
                letterSpacing = 2.sp,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MiddleBar(){
    tempRoute.value=Route()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Image(
            painter = painterResource(id = R.drawable.bus_ticket_booking),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        );

        Spacer(30.dp)

        var fromPlace by remember{
            mutableStateOf("")
        }
        var toPlace by remember {
            mutableStateOf("")
        }
        val scope = rememberCoroutineScope()

        Text("Book your Ticket!",
            style = TextStyle(
                color = Color.Black,
                fontSize = 25.sp,
                fontWeight = FontWeight.Light,
            )
            )

        Spacer(20.dp);

        fromPlace=textField("From")
        Spacer(20.dp)
        toPlace=textField("To")
        Spacer(30.dp)

        Button(
            onClick = {
                      if(fromPlace.isNotBlank() && toPlace.isNotBlank()
                          && isPureWord(fromPlace) && isPureWord(toPlace)
                          ){
                          scope.launch {
                              passenViewModel.getBuses(fromPlace, toPlace)
                          }
                          passenNavCon.navigate("BusesScreen")
                      }
            },
            modifier = Modifier.width(250.dp),
            shape = RoundedCornerShape(7.dp),
            colors = ButtonDefaults.buttonColors(Color(0xDC608ADF))
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(Icons.TwoTone.Search, "search buses", Modifier.size(20.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(7.dp))
                Text("Search buses")
            }
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
                        passenNavCon.graph.startDestinationId, inclusive = true
                    ).build()
                    passenNavCon.navigate("HomeScreen", options)
                },
                modifier = Modifier.size(45.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home), "Home Screen",
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
                    onClick={
                            passenNavCon.navigate("TicketsScreen")
                    },
                    modifier = Modifier.size(55.dp)
                ){
                Image(
                    painter = painterResource(id = R.drawable.ticket), "tickets",
                    modifier = Modifier.size(55.dp)
                )
            }
                Text("Tickets")
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                          passenNavCon.navigate("AccountScreen")
                },
                modifier = Modifier.size(55.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user), "",
                    modifier = Modifier.size(45.dp)
                )
            }
            Text("Account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusesScreen(){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomBar()
        },
        topBar = {
            Header(text = "Buses")
            Spacer(10.dp)
        }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val noRoutes by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.no_buses)
            )
            val routes = passenViewModel.routes.value

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (routes.isNotEmpty()) {
                    items(routes) { route ->
                        RouteDetails(route)
                        Spacer(10.dp)
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LottieAnimation(
                                composition = noRoutes,
                                renderMode = RenderMode.AUTOMATIC,
                                iterations = Integer.MAX_VALUE,
                                isPlaying = true,
                                modifier = Modifier.size(100.dp)
                            )

                            Text(
                                "No buses to book! ",
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Light
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteDetails(route: Route){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(start = 10.dp, end = 10.dp)
                .clickable() {
                    tempRoute.value = route
                    passenNavCon.navigate("PaymentScreen")
                },
            colors = CardDefaults.elevatedCardColors(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_directions_bus_24),
                        "bus icon",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xC34C76DA)
                    )

                    Text(route.number,
                        style = TextStyle(
                        fontWeight = FontWeight.SemiBold, fontSize = 20.sp
                        )
                    )
                }

                com.example.mypmt.users.passenger.Divider()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("From : ${route.from}", fontSize = 19.sp)
                    Text("To : ${route.to}", fontSize = 19.sp)
                    Text("Time : ${route.time}", fontSize = 19.sp)
                    Text("Bus code : ${route.code}", fontSize = 19.sp)
                    Text("Price : ${route.price}", fontSize = 19.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val route = tempRoute.value
        var quantity by remember{
            mutableStateOf(1)
        }
        var isSelected by remember{
            mutableStateOf(false)
        }

        Header("Payment")

        Spacer(20.dp)

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(start = 10.dp, end = 10.dp),
            colors = CardDefaults.elevatedCardColors(Color.White),
            elevation = CardDefaults.elevatedCardElevation(10.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp),
            ){
                Spacer(10.dp)
                Text("Ticket Details",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    )

                Spacer(16.dp)

                Text("From: ${route.from}", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text("To: ${route.to}", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text("Price: ${route.price}", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text("Bus Code: ${route.code}", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text("Person Id: ${passenViewModel.passenger.value.phoneNumber}", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text("Time: ${route.time}", fontSize = 16.sp, fontWeight = FontWeight.Light)
                Text("Quantity: ${quantity}", fontSize = 16.sp, fontWeight = FontWeight.Light)
            }
        }

        Spacer(20.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            ElevatedCard(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                colors = CardDefaults.elevatedCardColors(Color.White),
                elevation = CardDefaults.elevatedCardElevation(5.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        quantity.toString(),
                        style = TextStyle(
                            fontSize = 17.sp
                        )
                    )
                }
            }

            FloatingActionButton(
                onClick={
                  quantity++
                },
                shape =  RoundedCornerShape(30.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, "increase quantity", tint = Color(0xDC608ADF),
                    modifier = Modifier.size(30.dp)
                    )
            }
            FloatingActionButton(
                onClick={
                        if(quantity>1){
                            quantity--;
                        }
                },
                shape =  RoundedCornerShape(30.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, "reduce quantity", tint = Color(0xDC608ADF),
                    modifier = Modifier.size(30.dp)
                    )
            }
        }

        Spacer(20.dp)

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .size(100.dp)
                .padding(start = 10.dp, end = 10.dp),
            colors = CardDefaults.elevatedCardColors(Color.White),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp),
            ){
                Text("Payment Method",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Spacer(5.dp)
                Divider()
                Spacer(10.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Wallet",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.Black
                        )
                    )
                    InputChip(
                        selected = isSelected,
                        onClick = {
                                  isSelected=!isSelected
                        },
                        label = {
                            Text("Select")
                        },
                    )
                }
            }
        }

        Spacer(40.dp)

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .size(100.dp)
                .padding(start = 10.dp, end = 10.dp),
            colors = CardDefaults.elevatedCardColors(Color.White),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Total Amount : ",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        )
                    Text("Rs.${quantity * route.price.toInt()}",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }

                Spacer(5.dp)
                Divider(modifier = Modifier.padding(start=20.dp, end=20.dp))
                Spacer(10.dp)

                Button(
                    onClick= {
                        if (!isSelected) {
                            toast("Select payment method")
                        } else if (passenViewModel.passenger.value.balance.toInt() < quantity * 10) {
                            toast("Wallet balance is not sufficient")
                        } else {
                            val ticket = Ticket(
                                fromSpot = route.from,
                                toSpot = route.to,
                                personId = passenViewModel.passenger.value.phoneNumber,
                                quantity = quantity.toString(),
                                price = (route.price.toInt() * quantity).toString(),
                                time = route.time,
                                busCode = route.code
                            )
                            passenViewModel.bookTicket(ticket)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xDC608ADF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .height(40.dp)
                ){
                    Text("Pay", fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreen() {
    val person = passenViewModel.passenger.value
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
                painter = painterResource(R.drawable.user_info),
                contentDescription = "user information",
                modifier = Modifier.size(150.dp)
            )

            Spacer(10.dp)

            Text(
                person.firstName + " " + person.lastName,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light
                )
            )

            IconButton(
                onClick = {
                    try {
                        toast("Signed out successfully!")
                        if (sharedPref.getString("user", "") == "passenger") {
                            try {
                                sharedPref.edit().remove("user")
                                    .remove("phoneNumber").apply()
                            } catch (e: Exception) {
                                toast(e.message.toString())
                            }
                        }
                        activity.startActivity(
                            Intent(
                                activity.applicationContext,
                                MainActivity::class.java
                            )
                        )
                        activity.finish()
                    } catch (e: Exception) {
                        toast(e.message.toString())
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 5.dp)
                    .align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_logout_24),
                    contentDescription = "logout", tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(30.dp)

        InfoCard("First Name", R.drawable.peron_icon, person.firstName)
        Spacer(10.dp)
        InfoCard("Last Name", R.drawable.peron_icon, person.lastName)
        Spacer(10.dp)
        if (person.gender.lowercase() == "male") {
            InfoCard("Gender", R.drawable.man_icon, person.gender)
        } else {
            InfoCard("Gender", R.drawable.girl_icon, person.gender)
        }
        Spacer(10.dp)
        InfoCard("Phone Number", R.drawable.phone_icon, person.phoneNumber)
        Spacer(10.dp)
        InfoCard("Balance", R.drawable.baseline_account_balance_24, person.balance)
    }
}

@Composable
fun InfoCard(title: String, iconResId: Int, data: String) {
    var isAdd by remember {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            title,
            modifier = Modifier.padding(start = 35.dp),
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light
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
                    Icon(painter = painterResource(iconResId), title)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))
                    com.example.mypmt.users.passenger.Divider()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        data,
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        )
                    )
                    if (isPureNumber(data) && title=="Balance") {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = {
                                isAdd=!isAdd
                            }
                        ) {
                            Icon(Icons.TwoTone.Add, "add amount")
                        }
                    }
                }
                AnimatedVisibility(visible = isAdd) {
                    var amount by remember {
                        mutableStateOf("")
                    }
                    Dialog(
                        onDismissRequest = {/*TODO*/ },
                    ) {
                        Card(
                            modifier = Modifier.size(300.dp),
                            colors = CardDefaults.cardColors(Color.White),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Add Amount",
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = TextStyle(
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                )

                                amount = textField(text = "Enter Amount")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            isAdd = !isAdd
                                        },
                                        modifier = Modifier.size(width = 100.dp, height = 40.dp),
                                        colors = ButtonDefaults.buttonColors(Color(0xDC608ADF)),
                                        elevation = ButtonDefaults.buttonElevation(20.dp)
                                    ) {
                                        Text(
                                            "Cancel",
                                            fontWeight = FontWeight.Light,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            if (amount.length == 0 || !isPureNumber(amount) || amount == "0") {
                                                toast("Enter a valid amount!")
                                            } else {
                                                if (amount.toInt() < 0) {
                                                    toast("Enter a valid amount!")
                                                } else {
                                                    passenViewModel.addBalance(amount)
                                                    isAdd=!isAdd
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(width = 100.dp, height = 40.dp),
                                        colors = ButtonDefaults.buttonColors(Color(0xDC608ADF)),
                                        elevation = ButtonDefaults.buttonElevation(20.dp)
                                    ) {
                                        Text(
                                            "Add",
                                            fontWeight = FontWeight.Light,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TicketsScreen(){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Header("Tickets")
            Spacer(20.dp)
        },
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        val tickets by passenViewModel.tickets

        if (tickets.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No tickets booked!",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(tickets) { ticket ->
                        TicketCompose(ticket)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCompose(ticket: Ticket) {
    ElevatedCard(
        modifier = Modifier
            .size(width = 250.dp, height = 350.dp),
        colors = CardDefaults.elevatedCardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.Center,
        ){1
            Text("myPMT", modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(
                    fontSize = 20.sp,
                )
                )

            Spacer(20.dp)

            Text("From : ${ticket.fromSpot}")
            Spacer(2.dp)
            Text("To :  ${ticket.toSpot}")
            Spacer(2.dp)
            Text("Time : ${ticket.time}")
            Spacer(2.dp)
            Text("Quantity : ${ticket.quantity}", fontWeight = FontWeight.Bold)
            Spacer(2.dp)
            Text("Price : ${ticket.price}", fontWeight = FontWeight.Bold)
            Spacer(2.dp)
            Text("Person Id : ${ticket.personId}", fontWeight = FontWeight.Bold)
            Spacer(2.dp)
            Text("Bus Code : ${ticket.busCode}", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketBookedScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xDC608ADF)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(10.dp)
        Text("Ticket Booked Successfully!",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
            )
        Image(
            painter = painterResource(R.drawable.check),
            contentDescription = "ticket booked!",
            modifier = Modifier.size(100.dp)
        )
        ElevatedButton(
            onClick ={
                val options = NavOptions.Builder().setPopUpTo(
                    passenNavCon.graph.startDestinationId, inclusive = true
                ).build()
                passenNavCon.navigate("HomeScreen", options)
            },
            colors = ButtonDefaults.elevatedButtonColors(Color.White),
            elevation = ButtonDefaults.elevatedButtonElevation(10.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(20.dp)
                .height(50.dp)
        ){
            Text("Done",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp
                )
                )
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









