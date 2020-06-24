package com.triplecontrox.epsilon.site

import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.camera.Verification
import com.triplecontrox.epsilon.dataClasses.WebRequestBody
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.site.fragments.EqpFragment
import kotlinx.android.synthetic.main.activity_auth_screen.*
import java.util.ArrayList

class MaintenanceActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var shdPrf: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
        shdPrf = PreferenceManager.getDefaultSharedPreferences(this)

        val db = AppDatabase.getInstance(this).maintenanceDao()
        val site = db.getSites(intent.getStringExtra("SiteName"))
        title = resources.getString(R.string.equipmentOnSite)
        button = findViewById(R.id.signout)

        site.observe(this, Observer { siteReady(it) })
    }
    private fun siteReady(sitesNw: List<Maintenance>){
        val siteLocation = Location(sitesNw[0].SiteName)
        siteLocation.latitude = sitesNw[0].Latitude
        siteLocation.longitude = sitesNw[0].Longitude

        button.setOnClickListener {
            val location = Location(shdPrf.getString("address", "site"))
            location.latitude = shdPrf.getFloat("latitude", 0.0F).toDouble()
            location.longitude = shdPrf.getFloat("longitude", 0.0F).toDouble()

            if (location.distanceTo(siteLocation) < 50){
                // send a log out request to remote Watershed server
                val model = WebRequestBody(0, Location(""), sitesNw[0].SiteName,
                    sitesNw[0].Clocked_In, sitesNw[0].Assignee, "SITE",
                    "http://hr.watershedcorporation.com//epsilon-data/updateStatus.php")

                //send request body as intent to dispatcher activity
                val mIntent = Intent(this, Verification::class.java).apply {
                    putExtra("requestBody", model)
                }
                startActivity(mIntent)
            }
            else Snackbar.make(findViewById(android.R.id.content), "YOU ARE NOT ON SITE",
                Snackbar.LENGTH_SHORT).show()
        }

        val eqpFragment = EqpFragment()
        eqpFragment.arguments = Bundle().apply {
            putParcelableArrayList("equipment", sitesNw as ArrayList<Maintenance>)
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.recycler_frame, eqpFragment)
            commit()
        }
    }
}
