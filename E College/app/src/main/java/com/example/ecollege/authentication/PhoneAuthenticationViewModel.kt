package com.example.ecollege.authentication

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class PhoneAuthenticationViewModel(
    private val activity: Activity
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private var credential: AuthCredential? = null
    val currentUser = auth.currentUser
    private var verificationId = ""

    public fun sendVerificationCode(phoneNumber: String) {
        viewModelScope.launch {
            if (phoneNumber.isNotBlank()) {
                try {
                    val options = PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+91$phoneNumber")
                        .setActivity(activity)
                        .setTimeout(120, TimeUnit.SECONDS)
//                        .requireSmsValidation(true)p800
                        .setCallbacks(
                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    this@PhoneAuthenticationViewModel.credential = credential
                                    auth.signInWithCredential(credential)
                                        .addOnSuccessListener {
                                            toast("Sign in successfully!")
                                        }
                                        .addOnFailureListener{ error ->
                                            toast(error.message.toString())
                                        }
                                }
                                override fun onCodeSent(
                                    verificationId: String,
                                    p1: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    this@PhoneAuthenticationViewModel.verificationId = verificationId
                                    toast("Verification code sent!")
                                }
                                override fun onVerificationFailed(e: FirebaseException) {
                                    toast(e.message.toString())
                                }
                            }).build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                    return@launch
                } catch (e: Exception) {
                    toast(e.message.toString())
                }
            }
        }
    }

    public fun verifyCodeAndSignIn(smsCode: String) {
        viewModelScope.launch {
            if (smsCode.isNotBlank()) {
                try {
                    credential?.let {
                        auth.signInWithCredential(
                            PhoneAuthProvider.getCredential(
                                verificationId, smsCode
                            ))
                            .addOnSuccessListener { task ->
                                toast("Sign in successfully!")
                            }
                            .addOnFailureListener{ error ->
                                toast(error.message.toString())
                            }
                    }
                } catch (e: Exception) {
                    toast(e.message.toString())
                }
            }
        }
    }

    private fun toast(text: String){
        Toast.makeText(activity.applicationContext, text, Toast.LENGTH_LONG).show()
    }

}