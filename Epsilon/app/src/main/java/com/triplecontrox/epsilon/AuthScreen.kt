package com.triplecontrox.epsilon

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.triplecontrox.epsilon.authFragments.OfficeFragment
import com.triplecontrox.epsilon.authFragments.SiteFragment
import com.triplecontrox.epsilon.newuser.LoginActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthScreen : AppCompatActivity() {
    private lateinit var headerText: TextView
    private val choiceKey: String = "assignments"
    private lateinit var table: String
    private lateinit var username: String
    private lateinit var siteButton: RadioButton
    private lateinit var officeButton: RadioButton
    private lateinit var builder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_screen)

        builder = AlertDialog.Builder(this)
        siteButton = findViewById(R.id.site)
        officeButton = findViewById(R.id.office)
        headerText = findViewById(R.id.header)

        // get shared preferences and key value pairs for user and site names
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        table = sharedPreferences.getString(choiceKey, "SITE")!!
        username = sharedPreferences.getString("username", " ")!!

        switchFragments()

        // for the two radio buttons, change the shared preference value to the user's choice
        val radioGroup = findViewById<RadioGroup>(R.id.radioGrp)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val view  = group.findViewById(checkedId) as AppCompatRadioButton
            table = view.text as String
            sharedPreferences.edit().putString(choiceKey, table).apply()
            switchFragments()
        }

        LocalBroadcastManager.getInstance(this).also {
            it.registerReceiver(broadCastReceiver, IntentFilter("GOT LOCATION?"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(R.string.register_new_user)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val mIntent = Intent(this, LoginActivity::class.java)
//        mIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(mIntent)
        return super.onOptionsItemSelected(item)
    }

    // auto check the radio button that indicates user's previously saved choice
    private fun switchFragments(){
        var fragment: Fragment = SiteFragment(username)
        when(table){
            "SITE" -> siteButton.isChecked= true
            "OFFICE" -> {
                officeButton.isChecked = true
                fragment = OfficeFragment(username)
            }
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_holder, fragment)
            commit()
        }
    }

    private fun buildAlertMessageNoGps() {
        builder
            .setTitle("Device GPS Disabled")
            .setMessage("Will you like to enable your GPS?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        builder.create().apply { show() }
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "GOT LOCATION?"){
                if (intent.hasExtra("noSuccess"))
                    headerText.text = intent.extras!!.getString("noSuccess")
            }
        }
    }
    fun changeHeaderText(text: String) { headerText.text = text }

    fun runService(){
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra("username", username)

        GlobalScope.launch {
            startService(intent)
        }
    }

    fun checkPermissions(){
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) buildAlertMessageNoGps()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) runService()
        else
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), 100)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) runService()

            else ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }
}