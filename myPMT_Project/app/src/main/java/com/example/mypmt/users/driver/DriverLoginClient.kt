package com.example.mypmt.users.driver

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.KeyEventDispatcher.Component
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypmt.navCon
import com.example.mypmt.users.passenger.PassengerActivity
import com.example.mypmt.users.passenger.Person
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


class DriverLoginClient(private val activity: Activity) : ViewModel() {
    private val myAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val database by lazy{
        FirebaseFirestore.getInstance()
    }
    private val sharedPref by lazy{
        activity.getSharedPreferences("userCred", MODE_PRIVATE)
    }
    private val isOtpSent by lazy{
        mutableStateOf(false)
    }
    private val driver by lazy{
        mutableStateOf(Driver())
    }
    private val user = MutableStateFlow(myAuth.currentUser)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val verificationId = MutableStateFlow("")

    fun signIn(driver:Driver){
        viewModelScope.launch {
            sendVerificationCode(driver = driver)
        }
    }
    fun verifyCodeAndSignIn(otp: String, driver: Driver){
        viewModelScope.launch {
            signInWithCredentials(
                PhoneAuthProvider.getCredential(
                    verificationId.value, otp
                ), driver
            );
        }
    }
    fun getDriver(): Driver{
        return driver.value
    }


    private suspend fun sendVerificationCode(driver: Driver){
        try {
            if (!isVerifiedDriver(driver)) {
                toast("Invalid details!")
            } else {
                val options = PhoneAuthOptions.newBuilder()
                    .setPhoneNumber("+91${driver.phoneNumber}")
                    .setActivity(activity)
                    .setTimeout(120L, TimeUnit.SECONDS)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            toast("Otp sent to mobile number!")
                            this@DriverLoginClient.verificationId.value = verificationId
                            isOtpSent.value=true
                            navCon.navigate("OtpScreen")
                        }
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            toast("Phone number verified!")
                            viewModelScope.launch {
                                signInWithCredentials(credential, driver)
                            }
                        }
                        override fun onVerificationFailed(e: FirebaseException) {
                            toast(e.message.toString())
                        }
                    }).build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
        catch(e:Exception){
            toast(e.message.toString())
        }
    }
    private suspend fun signInWithCredentials(credential: PhoneAuthCredential, driver: Driver){
        try {
            myAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    toast("Sign in Success!")
                    try {
                        sharedPref.edit().putString("user", "Driver")
                            .putString("driverCode", driver.driverCode).apply()
                        isOtpSent.value=false
                        val i = Intent(activity.applicationContext, DriverActivity::class.java)
                        activity.finish()
                        activity.startActivity(i)
                    } catch (e: Exception) {
                        toast(e.message.toString())
                    }
                }
                .addOnFailureListener { task ->
                    toast(task.message.toString())
                }
                .await()
        }
        catch(e: Exception){
            toast(e.message.toString())
        }

    }

    private suspend fun isVerifiedDriver(driver: Driver): Boolean {
        val tempList= mutableListOf<Driver>()
        database.collection("drivers")
            .whereEqualTo("firstName", driver.firstName)
            .whereEqualTo("lastName", driver.lastName)
            .whereEqualTo("driverCode", driver.driverCode)
            .whereEqualTo("phoneNumber", driver.phoneNumber)
            .get().await()
            .documents.map{ document ->
                document.toObject<Driver>()?.let { tempList.add(it) }
            }
        if(tempList.isEmpty()){
            return false
        }
        else {
            this@DriverLoginClient.driver.value=tempList[0]
            return true
        }
    }

    private fun toast(text: String){
        Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show();
    }

}