package com.jayhymn.smartchat

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var phoneNumber: EditText
    private lateinit var verificationCode: EditText
    private lateinit var verifyBtn: Button

    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth
    internal var mVerificationId: String? = null

    private lateinit var mSendToken: PhoneAuthProvider.ForceResendingToken
    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        isLoggedIn()

        phoneNumber = findViewById(R.id.phone)
        verificationCode = findViewById(R.id.code)
        verifyBtn = findViewById(R.id.verifyBtn)

        verifyBtn.setOnClickListener {
            if (phoneNumber.text.equals("")){
                if (mVerificationId !== null){
                    verify()
                }
                startPhoneNumberVerification()
            }
        }

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
            }

            override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken) {
                mVerificationId = verificationId
                verifyBtn.text = "verify code"
                mSendToken = token
            }
        }
        getPermissions()
    }

    private fun verify(){
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, verificationCode.text.toString())
        signInWithPhoneAuthCredential(credential)
    }

    private fun startPhoneNumberVerification() {
        // takes parameters phone number, timeout duration, unit of timeout, context and a callback function
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber.text.toString()
            , 60, TimeUnit.SECONDS, this,
            mCallbacks)
        verifyBtn.text = "verify code"
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(
            this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(@NonNull task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        firstTimeUser()
                    } else {
                        if (task.getException() is FirebaseAuthInvalidCredentialsException) {

                        }
                    }
                }
            }
        )
    }
    private fun isLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(applicationContext, CoreActivty::class.java)
            startActivity(intent)
            finish()
            return
        }
    }
    private fun firstTimeUser() {
        val user = FirebaseAuth.getInstance().getCurrentUser()
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user!!.uid)
        val newUserPhoneNumber: String? = user.phoneNumber

        mDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val newUser:HashMap<String, String> = HashMap()
                    if (newUserPhoneNumber != null) {
                        newUser["name"] = newUserPhoneNumber
                        newUser["phone"] = newUserPhoneNumber
                        mDatabase!!.updateChildren(newUser as Map<String, Any>)
                    }
                }
                isLoggedIn()
            }
            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
            }
        )
    }
    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), 1)
        }
    }
}
