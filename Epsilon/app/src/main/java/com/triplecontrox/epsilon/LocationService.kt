package com.triplecontrox.epsilon

import android.Manifest.permission
import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.*
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.triplecontrox.epsilon.dataClasses.LocationModel
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.CountModel
import com.triplecontrox.epsilon.db.Maintenance
import java.util.*

class LocationService : Service() {
    private lateinit var queue: RequestQueue
    private lateinit var username: String
    private lateinit var geoCoder: Geocoder
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: AppDatabase
    private lateinit var siteName: String
    private lateinit var locationRequest: LocationRequest
    private var isClockedIn = false
    private var lAccuracy = 0
    private val url = "http://hr.watershedcorporation.com/epsilon-data/gpsdist.php"
    private var mNM: NotificationManager? = null
    private val noSuccess = arrayListOf(
        "you are not yet within any assigned site",
        "Invalid Request!", "you have no assignments for this period"
    )

    @Nullable
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =  START_STICKY

    override fun onCreate() {
        db = AppDatabase.getInstance(this)
        geoCoder = Geocoder(this, Locale.getDefault())
        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        queue = newRequestQueue(this)

        username = sharedPreferences.getString("username", " ")!!

        if (checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(Activity(), arrayOf(permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION), 100)
        }

        locationRequest =
            LocationRequest()
                .setInterval(6 * 60 * 1000)
                .setSmallestDisplacement(10F)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, mLocationCallBack, Looper.myLooper())
    }

    private fun currentAddress(latitude: Double, longitude: Double): String {
        if (latitude <= 90 && latitude >= -90 && longitude <= 180 || longitude >= -180) {
            try {
                val sb = StringBuilder()
                val addresses = geoCoder.getFromLocation(latitude, longitude,1)
                return if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    for (i in 0..address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i))
                    }
                    sb.toString()
                } else "No address available"
            }
            catch (e: Exception) {
                Log.d("LocationError", e.message)
            }
        }
        return "No address available"
    }

    private fun showNotification(title: String, content: String, shouldRepeat: Boolean){
        val notificationIntent = Intent(this, AuthScreen::class.java)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, "epsilonService")
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setSmallIcon(R.drawable.ic_notification_important_24px)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent).build()

        notification.flags = Notification.FLAG_AUTO_CANCEL

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel =
                NotificationChannel("epsilonService", "Epsilon",
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "notifies user to take an action"
                }
            mNM?.createNotificationChannel(notificationChannel)
        }

        if (shouldRepeat) {
            notification.flags = Notification.FLAG_INSISTENT
            mNM?.notify(1386, notification)

            Handler(Looper.getMainLooper()).postDelayed({
                mNM?.cancel(1386)
                notification.flags = Notification.FLAG_ONLY_ALERT_ONCE
                mNM?.notify(1386, notification) }, 15000)
            return
        }
        mNM?.notify(1386, notification)
    }

    private fun urlRequest(long: Double, lat: Double){
        val request = object : StringRequest(Method.POST, url,
            Response.Listener {
                Log.d("AWSRecognition", it)
                if (noSuccess.find{tag -> tag == it} == null){
                    // get and store assignments into the database
                    Log.d("AWSRecognition", it)
                    if (returnTable() == "SITE"){
                        val result =
                            Gson().fromJson(JsonParser().parse(it), Array<Maintenance>::class.java)
                        db.maintenanceDao().insertMaintenance(result)

                        showNotification("FOUND LOCATIONS", "tap this notification to view details",
                            false)
                        stopSelf()
                    }
                }
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent("GOT LOCATION?").putExtra("noSuccess", it))
            },
            Response.ErrorListener { Log.d("pass", it.toString()) })
        {
            override fun getParams() =
                hashMapOf(
                    Pair("Longitude", long.toString()),
                    Pair("Latitude", lat.toString()),
                    Pair("Assignee", username),
                    Pair("P", "XemPl1fy"),
                    Pair("table", returnTable())
                )

            override fun getHeaders(): MutableMap<String, String> {
                val params = hashMapOf(
                    Pair("Content-Type", "application/json")
                )
                return super.getHeaders()
            }
        }
        request.retryPolicy = DefaultRetryPolicy(60000, 0,1.0F)
        queue.add(request)
    }

    private fun storeLocation(latitude: Double, longitude: Double, address: String) {
        Firebase.database.reference.child("location").child(username).push()
            .setValue(LocationModel(ServerValue.TIMESTAMP, latitude, longitude, address))
    }

    private fun reVerify(){
        val delay = ((2 * 60 * 60 * 1000) + Random().nextInt(30 * 60 * 1000)).toLong()

        val task = object: TimerTask() {
            override fun run() {
                Thread{
                    if(db.officeDao().verificationCount(siteName) < 5){
                        db.officeDao().shouldVerify(siteName)
                        Handler(Looper.getMainLooper()).post {
                            if (returnTable() == "OFFICE" && isClockedIn) {
                                showNotification("VERIFICATION",
                                    "you need to verify identity", true)
                            }
                        }
                    }
                }.start()
            }
        }

        Timer().schedule(task, delay, delay)
    }

    private val mLocationCallBack =  object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
//            if (locationResult.lastLocation.accuracy < 150){
                val location = locationResult.lastLocation
                val count = getCount()
                val hasNoEntries = count.hasNoEntries!! < 1

                siteName = count.SiteName.toString()
                isClockedIn = count.Count_Clocked_In!! > 0

                sharedPreferences.edit()
                    .putString("address", currentAddress(location.latitude, location.longitude))
                    .putFloat("accuracy", location.accuracy)
                    .putFloat("latitude", location.latitude.toFloat())
                    .putFloat("longitude", location.longitude.toFloat()).apply()

                Log.d("OfficeActivity", sharedPreferences.getString("address", " ")!! +
                        " lat: ${sharedPreferences.getFloat("latitude", 0F).toDouble()}" +
                        " long: ${sharedPreferences.getFloat("longitude", 0F).toDouble()}" +
                        " accuracy: ${sharedPreferences.getFloat("accuracy", 0F)}")

                if (returnTable() == "SITE") {
                    if(hasNoEntries) {
                        urlRequest(location.longitude, location.latitude)
                    }
                    else if (isClockedIn) {
                        val address = currentAddress(location.latitude, location.longitude)
                        storeLocation(location.longitude, location.latitude, address)
                    }
                    return
                }
                else if (isClockedIn && !hasNoEntries){
                    reVerify()
                }
            }
//        }
    }

    private fun getCount(): CountModel {
        return if(returnTable() == "SITE") db.maintenanceDao().count()
        else db.officeDao().count()
    }
    private fun returnTable(): String{
        return sharedPreferences.getString("assignments", "SITE")!!
    }
}