package com.example.mypmt.users.passenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypmt.navCon
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class PassengerLogInClient(private val activity: Activity) : ViewModel() {

    private val myAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val verificationId = MutableStateFlow("")
    private val database by lazy{
        FirebaseFirestore.getInstance()
    }
    private val sharedPref by lazy{
        activity.getSharedPreferences("userCred", Context.MODE_PRIVATE)
    }
    private val isOtpSent by lazy{
        mutableStateOf(false)
    }
    private val passenger = MutableStateFlow(Person())

    private val user = MutableStateFlow(myAuth.currentUser)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun signIn(phoneNumber: String){
        viewModelScope.launch{
            try {
                if (existingPassenger(phoneNumber)) {
                    sendVerificationCode(Person(phoneNumber = phoneNumber))
                } else {
                    toast("The passenger record does not exists. Sign up instead!")
                }
            }
            catch (e: Exception){
                toast(e.message.toString())
            }
        }
    }
    fun signUp(person: Person){
        viewModelScope.launch {
            try {
                if (!existingPassenger(person.phoneNumber)) {
                    sendVerificationCode(person)
                } else {
                    toast("The passenger phone number already exists. Log in to continue!")
                }
            }
            catch(e: Exception){
                toast(e.message.toString())
            }
        }
    }
    fun verifyCodeAndSignIn(otp: String, person: Person){
        viewModelScope.launch {
            signInWithCredentials(
                PhoneAuthProvider.getCredential(
                    verificationId.value, otp
                ), person
            );
        }
    }
    fun getPassenger(): Person{
        return passenger.value
    }
    fun isOtpSent(): Boolean{
        return isOtpSent.value
    }

    private suspend fun sendVerificationCode(person: Person){
        try {
            val options = PhoneAuthOptions.newBuilder(myAuth)
                .setActivity(activity)
                .setPhoneNumber("+91${person.phoneNumber}")
                .setTimeout(120L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        toast("OTP sent to phone number!")
                        this@PassengerLogInClient.verificationId.value = verificationId
                        this@PassengerLogInClient.passenger.value = person
                        isOtpSent.value = true
                        navCon.navigate("OtpScreen")
                    }

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        toast("Phone number verified!")
                        viewModelScope.launch {
                            signInWithCredentials(credential, person)
                        }
                    }

                    override fun onVerificationFailed(error: FirebaseException) {
                        toast(error.message.toString())
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        catch(e: Exception){
            toast(e.message.toString())
        }
    }
    private suspend fun signInWithCredentials(credential: PhoneAuthCredential, person: Person){
        try {
            myAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        if (!existingPassenger(person.phoneNumber)) {
                            person.gender = person.gender.uppercase()
                            database.collection("passengers")
                                .add(person).await()
                            toast("Sign in Success!")
                        }
                    }
                    try {
                        sharedPref.edit().putString("user", "passenger")
                            .putString("phoneNumber", person.phoneNumber).apply()
                        isOtpSent.value=false
                        val i = Intent(activity.applicationContext, PassengerActivity::class.java)
                        activity.finish()
                        activity.startActivity(i)

                    } catch (e: Exception) {
                        toast(e.message.toString())
                    }
                }
                .addOnFailureListener { task ->
                    toast(task.message.toString())
                }.await()
            isOtpSent.value = false
        }
        catch(e: Exception){
            toast(e.message.toString())
        }
    }
    private suspend fun existingPassenger(phoneNumber: String): Boolean{
        try {
            val tempList = mutableListOf<Person>();
            database.collection("passengers")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get().await()
                .documents.map { document ->
                    document.toObject<Person>()?.let { tempList.add(it) }
                }
            return if (tempList.isEmpty()) {
                false
            } else {
                true
            }
        }
        catch(e: Exception){
            toast(e.message.toString())
            return false
        }
    }

    private fun toast(text: String){
        Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show();
    }

}

