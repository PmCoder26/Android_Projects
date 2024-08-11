package com.example.mypmt.users.passenger

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.example.mypmt.pmtData.Bus
import com.example.mypmt.pmtData.Route
import com.example.mypmt.pmtData.Ticket
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar

class PassengerViewModel(activity: Activity) : ViewModel() {

    private val database by lazy{
        FirebaseFirestore.getInstance()
    }
    private val sharedPref by lazy{
        activity.getSharedPreferences("userCred", MODE_PRIVATE)
    }
    private val phoneNumber = MutableStateFlow(
        sharedPref.getString("phoneNumber", "").toString()
    )
    val passenger by lazy{
        mutableStateOf(Person())
    }
    private val ticketCollection by lazy{
        database.collection("tickets")
    }
    val passengerCollection by lazy{
        database.collection("passengers")
    }
    val busCollection by lazy {
        database.collection("buses")
    }
    private val passenDocId by lazy{
        mutableStateOf("")
    }
    val tickets by lazy{
        mutableStateOf(mutableListOf<Ticket>())
    }
    val routes by lazy{
        mutableStateOf(mutableListOf<Route>())
    }

    init{
        viewModelScope.launch {
            try {
                val tempList = mutableListOf<String>()
                passengerCollection.whereEqualTo("phoneNumber", phoneNumber.value)
                    .get().await().map { document ->
                        tempList.add(document.id.toString())
                    }
                if (tempList.isEmpty()) {
                    toast("Failed to fetch details!")
                } else {
                    passenDocId.value = tempList[0]
                }
                getPassenger()
                updateTickets()
            }
            catch(e:Exception){
                toast(e.message.toString())
            }
        }
    }

    public fun getBuses(from: String, to: String){
        viewModelScope.launch {
            try {
                busCollection.addSnapshotListener { snapShot, error ->
                    error?.let {
                        toast(error.message.toString())
                    }
                    val list = mutableListOf<Route>()
                    snapShot?.let { documents ->
                        val dateFormat = SimpleDateFormat("HH:mm")
                        val tempBuses = documents.toObjects<Bus>()
                        tempBuses.forEach { bus ->
                            val calendar = Calendar.getInstance()
                            var tempTo = ""
                            var tempFrom = ""
                            var cost = 0
                            var newTime = ""
                            val currTime = dateFormat.parse(bus.currentTime)
                            calendar.time = currTime
                            for (stop in bus.busStops!!) {
                                if (tempFrom.isBlank()) {
                                    if (stop == from) {
                                        tempFrom = from
                                    }
                                } else if (tempFrom.isNotBlank() && tempTo.isBlank()) {
                                    if (stop != to) {
                                        cost += 6
                                    } else if (stop == to) {
                                        cost += 6
                                        tempTo = stop
                                        break;
                                    }
                                }
                            }
                            if (tempFrom.isNotBlank() && tempTo.isNotBlank()) {
                                bus?.let {
                                    bus.busStops?.indexOf(tempFrom)
                                        ?.let { calendar.add(Calendar.MINUTE,  it * 10) }
                                    newTime = dateFormat.format(calendar.time) ?: ""
                                    list.add(
                                        Route(
                                            to = to, from = from,
                                            price = cost.toString(),
                                            code = bus.code,
                                            number = bus.number,
                                            time = newTime
                                        )
                                    )
                                }
                            }
                        }
                        routes.value = list
                    }
                }
            }
            catch(e: Exception){
                toast(e.message.toString())
            }
        }
    }
    public fun bookTicket(ticket: Ticket) {
        viewModelScope.launch {
            try {
                ticketCollection.add(ticket)
                    .addOnSuccessListener {
                        database.runTransaction { transaction ->
                            val passenRef = passengerCollection.document(passenDocId.value)
                            val balance = transaction.get(passenRef)["balance"].toString().toInt()
                            transaction.update(
                                passenRef,
                                "balance",
                                (balance - ticket.price.toInt()).toString()
                            )
                        }
                        passenNavCon.navigate("TicketBookedScreen")
                    }
                    .addOnFailureListener { task ->
                        toast(task.message.toString())
                    }
            } catch (e: Exception) {
                toast(e.message.toString())
            }
        }
    }
    public fun addBalance(balance: String) {
        viewModelScope.launch {
            try {
                val personRef = passengerCollection.document(passenDocId.value)
                passengerCollection.document(passenDocId.value)
                    .update("balance",
                        (personRef.get().await()["balance"].toString().toInt() + balance.toInt()).toString())
                    .await()
                toast("Amount added successfully!")
            } catch (e: Exception) {
                toast(e.message.toString())
            }
        }
    }

    private fun getPassenger(){
        try {
        if(phoneNumber.value==""){
            toast("Passenger record doesn't exists!");
        }
        else {
            var tempPerson = Person()
            passengerCollection.document(passenDocId.value.toString())
                .addSnapshotListener { person, error ->
                    error?.let {
                        toast(error.message.toString())
                    }
                    person?.let {
                        tempPerson = Person(
                            person.get("firstName").toString(),
                            person.get("lastName").toString(),
                            person.get("gender").toString(),
                            person.get("phoneNumber").toString(),
                            person.get("balance").toString()
                        )
                    }
                    viewModelScope.launch {
                        passenger.value = tempPerson
                    }
                }
            }
        }
        catch(e: Exception){
            toast(e.message.toString())
        }
    }
    private suspend fun getTickets(){
        if(passenDocId.value.isBlank()){
            toast("Failed to fetch details!")
        }
        else{
            var tempList = mutableListOf<Ticket>();
            ticketCollection
                .whereEqualTo("personId", passenger.value.phoneNumber)
                .get().await().documents.forEach{ ticket ->
                    ticket.toObject<Ticket>()?.let { tempList.add(it) }
                }
            tickets.value=tempList
        }
    }
    private suspend fun updateTickets(){
        ticketCollection
            .addSnapshotListener{ snapshot, error ->
            error?.let{
                toast(error.message.toString())
            }
            snapshot?.let{
                viewModelScope.launch {
                    getTickets()
                }
            }
        }
    }


    private fun toast(text: String){
        Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show()
    }

}