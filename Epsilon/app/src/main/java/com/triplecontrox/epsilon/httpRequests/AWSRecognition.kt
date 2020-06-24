
package com.triplecontrox.epsilon.httpRequests

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.amazonaws.services.rekognition.model.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.triplecontrox.epsilon.AuthScreen
import com.triplecontrox.epsilon.dataClasses.LocationModel
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.Office
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class AWSRecognition(private val context: Context, private val body: HashMap<String, String>, vararg bytes: ByteArray?) {
    private val db = AppDatabase.getInstance(context)
    private val queue = Volley.newRequestQueue(context)
    private val sentImage = Image().withBytes(ByteBuffer.wrap(bytes[0]))

    suspend fun request(){
        amazonRecognition.setRegion(Region.getRegion(Regions.US_EAST_2))

        withContext(Dispatchers.IO){
            val verifyFacesRequest =
                CompareFacesRequest().withSourceImage(sentImage).withTargetImage(target)

            // Call operation
            try {
                val verifyFacesResult = amazonRecognition.compareFaces(verifyFacesRequest)
                val faceMatches = verifyFacesResult.faceMatches
                val pass = faceMatches.find { it.face.confidence >90f }

                if (pass != null){
                    urlRequest(body)
                    return@withContext
                }
                showToast("face doesn't match")
            }
            catch (e: InvalidParameterException){ showToast("can't detect any faces in the photo sent") }
            catch (e: InvalidS3ObjectException){ showToast(
                "${body["Assignee"]} hasn't uploaded any photo for verification")}
            catch (e: AmazonClientException){ showToast("check internet connection and try again") }
        }
    }
    private suspend fun showToast(toastMsg: String){
        withContext(Main){ Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show() }
    }

    private fun urlRequest(model: HashMap<String, String>){
        val request = object : StringRequest(Method.POST, model["url"],
            Response.Listener { response ->
                Log.d("AWSRecognition", response)
                if (response == "success!") {
                    GlobalScope.launch {
                        resolveRequest(model)
                        Firebase.database.reference.child("Location")
                            .child(body["Assignee"]!!).push().setValue(
                                LocationModel(ServerValue.TIMESTAMP, body["Latitude"]!!.toDouble(),
                                    body["Longitude"]!!.toDouble(), "${body["Address"]}")
                            )
                        startNewActivity(context)
                    }
                }
            },
            Response.ErrorListener { Toast.makeText(context, it.message, Toast.LENGTH_LONG).show() })
        {
            override fun getParams() = model

            override fun getHeaders(): MutableMap<String, String> {
                hashMapOf(Pair("Content-Type", "application/json"))
                return super.getHeaders()
            }
        }
        request.retryPolicy = DefaultRetryPolicy(60000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(request)
    }

    private suspend fun resolveRequest(hashMap: HashMap<String, String>){
        when(hashMap["table"]){
            "OFFICE" -> {
                if (hashMap["Clocked_In"] == "NO TIME"){
                    hashMap["Clocked_In"] = "10:00:00"
                    hashMap["dailyDeals"] = "NO"
                    hashMap["otherDeals"] = "NO"
                    hashMap["support"] = "NO"

                    Log.d("hash", hashMap.toString())

                    val officeJSON = Gson().toJson(hashMap)
                    val office = Gson().fromJson(officeJSON, Office::class.java)
                    db.officeDao().insertOffice(office)
                }
                else db.officeDao().deleteOffice(hashMap["Id"]!!.toInt())
            }
            "SITE" -> {
                if (hashMap["Clocked_In"] == "00:00:00") {
                    db.maintenanceDao().clockIn(hashMap["SiteName"]!!)
                }
                else db.maintenanceDao().deleteSite(hashMap["SiteName"]!!)
            }
            "RE_VERIFY" -> db.officeDao().verification()
        }
    }

    // launch new activity after successfully storing user login to the database
    private suspend fun startNewActivity(context: Context){
        showToast("Face successfully verified")

        val intent = Intent(context, AuthScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        (context as Application).startActivity(intent)
    }

    private var amazonRecognition = AmazonRekognitionClient(object : AWSCredentials {
        override fun getAWSAccessKeyId() = "AKIA5UDSNLC3553DVQUR"
        override fun getAWSSecretKey() = "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"
    })

    private val target = Image().withS3Object(S3Object().withName("${body["Assignee"]}.jpg")
        .withBucket("3controx-collection"))
}