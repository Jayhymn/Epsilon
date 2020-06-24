package com.triplecontrox.epsilon.office

import android.app.NotificationManager
import android.content.*
import android.location.Location
import android.net.Network
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.triplecontrox.epsilon.AuthScreen
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.camera.Verification
import com.triplecontrox.epsilon.dataClasses.BreakFirebase
import com.triplecontrox.epsilon.dataClasses.DistinctOffice
import com.triplecontrox.epsilon.dataClasses.LocationModel
import com.triplecontrox.epsilon.dataClasses.WebRequestBody
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.Office
import com.triplecontrox.epsilon.db.OfficeDao
import com.triplecontrox.epsilon.newuser.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class OfficeActivity : AppCompatActivity() {
    private lateinit var builder: AlertDialog
    private lateinit var numberPicker: NumberPicker
    private var Id: Int = 0
    private lateinit var textView: TextView
    private lateinit var countView: TextView
    private lateinit var site: Office
    private lateinit var db: OfficeDao
    private var postValue: Int = 0
    private lateinit var notificationManager: NotificationManager
    private val tasks = JSONObject()
    private lateinit var button: Button
    private lateinit var broadcast: LocalBroadcastManager
    private lateinit var shdPrf: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_office)
        shdPrf = PreferenceManager.getDefaultSharedPreferences(this)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        db = AppDatabase.getInstance(this).officeDao()

        countView = findViewById(R.id.count)
        Id = intent.getIntExtra("UID", 0)

        val siteWatch = db.getOffices(Id)

        siteWatch.observe(this, Observer {
            site = it
            Log.d("OfficeActivity", site.toString())
            if (site.shouldVerify == 1){
                startActivity(Intent(this@OfficeActivity, AuthScreen::class.java))
                finish()
            }
            else{
                Log.d("AWSRec", it.toString())
                siteReady()
            }
        })

        broadcast =
            LocalBroadcastManager.getInstance(this).also {
                it.registerReceiver(broadCastReceiver, IntentFilter("changed time"))
            }

        textView = TextView(this).apply {
            text = context.getString(R.string.choose_break)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            textSize = 15F
            background = resources.getDrawable(R.drawable.border_bottom)
            setTextColor(resources.getColor(R.color.colorPrimary))
            setPadding(2, 10, 2, 10)
            }

        // create number picker and set value change listener
        numberPicker = NumberPicker(this)
        numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // create and prepare alert builder
        builder =
            AlertDialog
            .Builder(this)
            .setCustomTitle(textView)
            .setView(numberPicker)
            .setPositiveButton("SET") { dialog, _ ->
                GlobalScope.launch {
                    runCountDown(dialog)
                }
            }
            .setNegativeButton("CANCEL"){ dialog, _ ->
                dialog.dismiss()
            }
                .setCancelable(true)
                .create()

        button = findViewById(R.id.signout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(R.string.register_new_user)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mIntent = Intent(this, LoginActivity::class.java)
        startActivity(mIntent)
        return super.onOptionsItemSelected(item)
    }

    private fun siteReady(){
        val siteLocation = Location("siteLocation")
        siteLocation.latitude = site.Latitude
        siteLocation.longitude = site.Longitude

        button.setOnClickListener {
            val location = Location(shdPrf.getString("address", "office"))
            location.latitude = shdPrf.getFloat("latitude", 0.0F).toDouble()
            location.longitude = shdPrf.getFloat("longitude", 0.0F).toDouble()

            Log.d("OfficeActivityDistance", location.distanceTo(siteLocation).toString())
            Log.d("OfficeActivitySite", "Lat: ${location.latitude} and Long: ${location.longitude}")
            Log.d("OfficeActivityLoc", " Lat: ${siteLocation.latitude} and Long: ${siteLocation.longitude}")

            if (siteLocation.distanceTo(location) < 50){
                //prepare server request body
                val body = WebRequestBody(site.Id, siteLocation, site.SiteName, site.Clocked_In, site.Assignee,
                    "OFFICE", "http://hr.watershedcorporation.com//epsilon-data/updateStatus.php")

                //send request body as intent to dispatcher activity
                val mIntent = Intent(this, Verification::class.java).apply {
                    putExtra("requestBody", body)
                }
                startActivity(mIntent)
            }
            else Snackbar.make(findViewById(android.R.id.content), "YOU ARE NOT IN THE OFFICE",
                Snackbar.LENGTH_SHORT).show()
        }

        tasks.put("DAILY DEALS", site.dailyDeals)
        tasks.put("OTHER DEALS", site.otherDeals)
        tasks.put("SUPPORT", site.support)
    }

    fun fillGoogleForm(view: View){
        val mIntent =
            Intent(this, GoogleFormActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        val viewText = (view as TextView).text as String
        val isDone = tasks.get(viewText) as String

        if (isDone == "NO"){
            mIntent.putExtra("details", site)
            mIntent.putExtra("task", viewText)

            startActivity(mIntent)
        }
        else{

            view.alpha = 0.3F
            Snackbar.make(findViewById(android.R.id.content), "$viewText TASK IS COMPLETE",
                Snackbar.LENGTH_SHORT).show()
        }
    }
    fun showDialog(view: View){
        val previousPick = site.breakTime

        val list = when(previousPick){
            60 -> arrayOf("0")
            40 -> arrayOf("20")
            20 -> arrayOf("20", "40")
            0 -> arrayOf("20", "25", "35", "40", "60")
            else -> arrayOf((60 - previousPick).toString())
        }

        val stringBuilder = StringBuilder()
        stringBuilder.appendln("")
        stringBuilder.appendln("CHOOSE HOW LONG YOU WILL YOU BE ON BREAK")
        stringBuilder.appendln("YOU HAVE ${60 -previousPick} MINUTES LEFT")

        textView.text = stringBuilder

        numberPicker.apply {
            maxValue = list.size - 1
            displayedValues = list
            value = numberPicker.maxValue / 2
            wrapSelectorWheel = true
        }
        postValue = list[numberPicker.value].toInt()
        numberPicker.setOnValueChangedListener{ _, _, newVal -> postValue = list[newVal].toInt() }

        builder.setView(numberPicker)
        builder.show()
    }

    private suspend fun runCountDown(dialog: DialogInterface){
        if (!(countView.text == "BREAK OVER" || countView.text == "")){
            Snackbar.make(findViewById(android.R.id.content), "BREAK IS NOT YET OVER",
                Snackbar.LENGTH_SHORT).show()
        }
        else{
            site.breakTime = (site.breakTime) + postValue

            withContext(Dispatchers.IO){
                if (db.addBreakTotal(site.breakTime.toString(), site.Id) > 0){
                    withContext(Dispatchers.Main){
                        Intent(applicationContext, BreakTimer::class.java).also {
                            it.putExtra("ticker", (postValue * 60 * 1000).toLong())
                            it.putExtra("office", site.SiteName)
                            startService(it)
                        }
                    }

                    Firebase.database.reference
                        .child("break")
                        .child(site.Assignee!!).push()
                        .setValue(BreakFirebase(ServerValue.TIMESTAMP, postValue))
                }
            }
            dialog.dismiss()
        }
    }
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra("ticking")!!){
                countView.text = intent.extras!!.getString("ticking")
            }
        }
    }

    override fun onDestroy() {
        broadcast.unregisterReceiver(broadCastReceiver)
        super.onDestroy()
    }
}
