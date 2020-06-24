package com.triplecontrox.epsilon

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import com.triplecontrox.epsilon.camera.SnappedDocs
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.db.Office
import com.triplecontrox.epsilon.office.GoogleFormActivity
import com.triplecontrox.epsilon.office.OfficeActivity
import com.triplecontrox.epsilon.site.SiteGoogleForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebAppInterface(
    private val context: Context,
    private val bundle: Bundle,
    private val task: String? = "") {
    private val db = AppDatabase.getInstance(context)

    /** Load another activity  */
    @JavascriptInterface
    fun siteForm() {
        val maintenance =  bundle.getParcelable<Maintenance>("details")!!

        GlobalScope.launch(Dispatchers.IO){
            db.maintenanceDao().updateSiteForm(maintenance.Id)
        }
        val intent = Intent(context, SnappedDocs::class.java)
        intent.putExtra("details", maintenance)
        context.startActivity(intent)
    }

    @JavascriptInterface
    fun officeForm(){
        val office = bundle.getParcelable<Office>("details")
        Log.d("task", task)
        GlobalScope.launch(Dispatchers.IO){
            when(task){
                "DAILY DEALS" -> db.officeDao().updateDaily(office?.Id!!)
                "OTHER DEALS" -> db.officeDao().updateOthers(office?.Id!!)
                else -> db.officeDao().updateSupport(office?.Id!!)
            }

            val mIntent = Intent(context, OfficeActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("UID", office?.Id)

            context.startActivity(mIntent)
        }
    }
}