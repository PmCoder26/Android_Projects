package com.example.mypmt.users.driver

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.text.format.Time
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypmt.pmtData.Bus
import com.example.mypmt.pmtData.BusRoute
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar

class DriverViewModel(activity: Activity) : ViewModel() {

    private val sharedPref by lazy{
        activity.getSharedPreferences("userCred", MODE_PRIVATE)
    }
    private val database by lazy{
        FirebaseFirestore.getInstance()
    }
    private val driverCollection by lazy{
        database.collection("drivers")
    }
    private val routesCollection by lazy{
        database.collection("busRoutes")
    }
    private val ticketCollection by lazy{
        database.collection("tickets")
    }
    private val busCollection by lazy{
        database.collection("buses")
    }
    private val busCodeCollection by lazy{
        database.collection("busCodes")
    }
    val driver by lazy{
        mutableStateOf(Driver())
    }
    val tempBus by lazy{
        mutableStateOf(Bus())
    }
    private val driverDocId by lazy{
        mutableStateOf("")
    }
    private val busDocId by lazy{
        mutableStateOf("")
    }
    private val driverCode by lazy{
        mutableStateOf(sharedPref.getString("driverCode", ""))
    }
    val isThereRoute by lazy{
        mutableStateOf(false)
    }
    val isValidBusCode by lazy{
        mutableStateOf(false)
    }

    init{
        viewModelScope.launch{
            try {
                val tempList = mutableListOf<String>();
                driverCollection
                    .whereEqualTo("driverCode", driverCode.value)
                    .get().await().forEach{ document ->
                        tempList.add(document.id)
                    }
                if(tempList.isEmpty()){
                    toast("Failed to fetch details")
                }
                else{
                    driverDocId.value=tempList[0]
                }
                getDriver()
            }
            catch(e: Exception){
                toast(e.message.toString())
            }
        }
    }

    fun getRoute(busNumber: String){
        try{
            viewModelScope.launch{
                val tempList = mutableListOf<BusRoute>()
                routesCollection
                    .whereEqualTo("number", busNumber)
                    .get().await().forEach{ route ->
                        tempList.add(route.toObject())
                    }
                if(tempList.isEmpty()){
                    toast("Invalid bus number!")
                    isThereRoute.value=false
                }
                else{
                    isThereRoute.value=true
                    tempBus.value=Bus(
                        number = tempList[0].number,
                        busStops = tempList[0].busStops,
                        code = tempBus.value.code,
                    )
                }
            }
        }
        catch(e: Exception){
            toast(e.message.toString())
        }
    }
    fun setBusRoute(busCode: String, isForwardRoute: Boolean){
        try {
            viewModelScope.launch {
                val busCodes = mutableListOf<String>()
                busCodeCollection
                    .whereEqualTo("busCode", busCode)
                    .whereEqualTo("number", tempBus.value.number)
                    .get().await().forEach{ document ->
                        busCodes.add(document["busCode"].toString())
                    }
                if(busCodes.isEmpty()){
                    toast("Invalid bus code!")
                    isValidBusCode.value=false
                }
                else {
                    isValidBusCode.value=true
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("hh:mm")
                    val strTime=dateFormat.format(calendar.time)
                    tempBus.value = Bus(
                        number = tempBus.value.number,
                        code = busCode,
                        busStops = tempBus.value.busStops,
                        currentTime = strTime,
                    )
                    if (!isForwardRoute) {
                        tempBus.value = Bus(
                            number = tempBus.value.number,
                            code = busCode,
                            busStops = tempBus.value.busStops?.asReversed(),
                            currentTime = strTime
                        )
                    }
                    busDocId.value = busCollection.add(tempBus.value).await().id
                    updateBus()
                    driverNavCon.navigate("RouteScreen")
                }
            }
        }
        catch(e: Exception) {
            toast(e.message.toString())
        }
    }
    fun updateTickets(){
        try{
            viewModelScope.launch {
                ticketCollection
                    .whereEqualTo("busCode", tempBus.value.code)
                    .get().await().forEach{ ticket ->
                        ticket?.let{
                            ticketCollection.document(ticket.id).delete().await()
                        }
                    }
            }
        }
        catch(e: Exception){
            toast(e.message.toString())
        }
    }
    fun updateBusRoute(){
        viewModelScope.launch {
            try{
                if(tempBus.value.busStops?.isEmpty()==true){
                    toast("No routes to update!")
                }
                else{
                    database.runBatch { batch ->
                        val busRef = busCollection.document(busDocId.value)
                        val dateFormat = SimpleDateFormat("hh:mm")
                        tempBus.value.busStops?.removeFirst()
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(tempBus.value.currentTime)
                        calendar.add(Calendar.MINUTE, 10);
                        batch.update(busRef, "busStops", tempBus.value.busStops)
                            .update(busRef, "currentTime", dateFormat.format(calendar.time).toString())
                    }.await()
                    if(tempBus.value.busStops?.isEmpty()==true){
                        updateTickets()
                        tempBus.value=Bus("", "", mutableListOf(), "",)
                        busCollection.document(busDocId.value).delete()
                        busDocId.value = ""
                    }
                }
            }
            catch(e: Exception){
                if(busDocId.value==""){
                    return@launch
                }
                toast(e.message.toString())
            }
        }
    }

    private fun getDriver(){
        try{
            var tempDriver = Driver()
            driverCollection.document(driverDocId.value)
                .addSnapshotListener{ driver, error ->
                    error?.let{
                        toast(error.message.toString())
                    }
                    driver?.let{
                        tempDriver = Driver(
                            driver.get("driverCode").toString(),
                            driver.get("firstName").toString(),
                            driver.get("lastName").toString(),
                            driver.get("phoneNumber").toString()
                        )
                    }
                    viewModelScope.launch {
                        this@DriverViewModel.driver.value=tempDriver
                    }
                }
        }
        catch(e: Exception){
            toast(e.message.toString())
        }
    }
    private fun updateBus(){
        try{
            viewModelScope.launch {
               val listner = busCollection.document(busDocId.value)
                    .addSnapshotListener{ bus, error ->
                        if(tempBus.value.busStops?.isEmpty()==true){
                            return@addSnapshotListener
                        }
                        error?.let{
                            if(busDocId.value==""){
                                return@addSnapshotListener
                            }
                            toast(error.message.toString())
                        }
                        bus?.let{
                            tempBus.value= bus.toObject<Bus>()!!
                        }
                    }
                if(tempBus.value.busStops?.isEmpty()==true){
                    listner.remove()
                }
            }
        }
        catch(e: Exception){
            if(busDocId.value==""){
                return
            }
            toast(e.message.toString())
        }
    }



    private fun toast(text: String){
        Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show()
    }

}