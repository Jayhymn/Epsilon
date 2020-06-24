package com.triplecontrox.epsilon

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.view.children
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.triplecontrox.epsilon.dataClasses.DeviceModel
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {
    private lateinit var telephonyManager: TelephonyManager
    private var imei = ""
    private val dropList =  mutableListOf<String>()
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var textView: TextView
    private lateinit var spinner: Spinner
    private lateinit var queue: RequestQueue
    private lateinit var progressBar: ProgressBar
    private lateinit var button: Button
    private var is_Blocked = false
    private lateinit var sharedPreferences: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        queue = Volley.newRequestQueue(this)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.INVISIBLE

        textView = findViewById(R.id.textView)

        button = findViewById(R.id.button)
        button.setOnClickListener{ selectName() }
        spinner = findViewById(R.id.spinner)

        spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, dropList)
        spinner.adapter = spinnerAdapter
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                button.isEnabled = position != 0 && !is_Blocked
            }

        }
        setImei()
        isRegistered()
        urlRequest()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setImei() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            }
            else{
                telephonyManager.deviceId
            }
            if (imei.isEmpty()) {
                setImei()
                if (spinner.count < 1){
                    urlRequest()
                }
            }
        } else {
            requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.FOREGROUND_SERVICE), 101)
        }
    }

    private fun goToAuthScreen(username: String){
        val intent = Intent(applicationContext, AuthScreen::class.java)

        sharedPreferences.edit().putString("username", username).apply()

        startActivity(intent)
        finish()
        return
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isRegistered() {
        if (imei !== "") {
            val database = Firebase.database.reference.child("devices")

            database.child(imei).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("status").value == "Active") {
                                goToAuthScreen(snapshot.child("username").value.toString())
                            } else {
                                is_Blocked = true
                                Toast.makeText(applicationContext, "device not unauthorized to use app", Toast.LENGTH_LONG).show()
                                finish()
                            }
                        } else is_Blocked = false
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(applicationContext, databaseError.message, Toast.LENGTH_LONG).show()
                        isRegistered()
                    }
                }
            )
        }
        else setImei()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                if (permissions.isNotEmpty()) setImei()
                else {
                    Toast.makeText(this, "App Cannot work without your permission", Toast.LENGTH_SHORT).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }
    }
    private fun urlRequest(){
        val url = "http://hr.watershedcorporation.com/epsilon-data/staff.php"
        //String Request initialized
        val volleyRequest = object : JsonArrayRequest(Method.GET, url,null,Response.Listener { response ->
            if (!dropList.isNullOrEmpty())
                dropList.clear()
            dropList.add("NO USERNAME SELECTED")
            for( i in 0 until response.length()){
                dropList.add(response.getString(i))
                spinnerAdapter.notifyDataSetChanged()
            }
            textView.text = getString(R.string.register_device)
        }, Response.ErrorListener { error ->
            if (error.toString() == "com.android.volley.TimeoutError")
                Toast.makeText(this, "Please check your internet connection and restart app",
                    Toast.LENGTH_SHORT).show()
        }) {}
        val socketTimeout = 20000
        val retryPolicy = DefaultRetryPolicy(socketTimeout, 4,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        volleyRequest.retryPolicy = retryPolicy
        volleyRequest.setShouldCache(true)

        queue.add(volleyRequest)
    }

    private fun selectName(){
        progressBar.visibility = View.VISIBLE
        val database = Firebase.database.reference
        val message = "couldn't register device but retrying..."
        database.child("devices").child(imei).setValue(DeviceModel(ServerValue.TIMESTAMP,
            "Active", spinner.selectedItem.toString()))
            .addOnSuccessListener { goToAuthScreen(spinner.selectedItem.toString()) }

            .addOnFailureListener {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                selectName()
            }
    }
}
