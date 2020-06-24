package com.triplecontrox.epsilon.newuser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.triplecontrox.epsilon.R

class LoginActivity : AppCompatActivity() {
    private val admin = Firebase.database.reference.child("Login")
    private lateinit var errorMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val login: Button = findViewById(R.id.login)
        val username : EditText = findViewById(R.id.username)
        val password :EditText = findViewById(R.id.password)
        errorMsg = findViewById(R.id.errorMessage)

        login.isEnabled = false
        login.setOnClickListener{ isCorrect(username.text.toString(), password.text.toString()) }

        username.doAfterTextChanged {
            login.isEnabled = !it.isNullOrEmpty() && !password.text.isNullOrEmpty()
        }

        password.doAfterTextChanged {
            login.isEnabled = !it.isNullOrEmpty() && !username.text.isNullOrEmpty()
        }
    }
    private fun isCorrect(name: String, passKey: String){
        admin.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChild(name) && snapshot.child(name).value == passKey) {
                        Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(applicationContext, UploadUserImage::class.java))
                        this@LoginActivity.finish()
                        return
                    }
                    errorMsg.text = getString(R.string.wrong_login)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(applicationContext, databaseError.message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}