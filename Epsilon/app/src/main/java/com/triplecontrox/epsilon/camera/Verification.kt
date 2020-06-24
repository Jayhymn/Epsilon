package com.triplecontrox.epsilon.camera

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.dataClasses.WebRequestBody
import com.triplecontrox.epsilon.httpRequests.AWSRecognition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

// dispatches user's request to server after he/she has been verified successfully
class Verification : AppCompatActivity() {
    private lateinit var file: File
    private lateinit var progressDialog: Dialog
    private val body = HashMap<String, String>()
    private val snap = CameraVerify(this, 101)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        val modelIntent: WebRequestBody = intent.getParcelableExtra("requestBody")
        Log.d("Verification", modelIntent.toString())

        body["Id"] = modelIntent.Id.toString()
        body["Assignee"] = modelIntent.assignee!!
        body["SiteName"] = modelIntent.siteName!!
        body["table"] = modelIntent.table!!
        body["P"] = "XemPl1fy"
        body["url"] = modelIntent.url!!
        body["Clocked_In"] = modelIntent.clockedIn!!
        body["Latitude"] = modelIntent.location?.latitude.toString()
        body["Longitude"] = modelIntent.location?.longitude.toString()

        body["Address"] = modelIntent.location?.provider.toString()

        val button: Button = findViewById(R.id.btnCamera)
        button.setOnClickListener { snap.takePicture() }

        // prepare progress bar
        progressDialog = AlertDialog.Builder(this).setView(R.layout.progress).create()
        progressDialog.setCancelable(false)
        snap.takePicture()
    }
    override fun onActivityResult(requestCode: Int, resultStatus: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultStatus, data)

        if (requestCode == 101 && resultStatus == RESULT_OK) {
            progressDialog.show()

            GlobalScope.launch(Dispatchers.IO) {
                AWSRecognition(applicationContext, body, compressImage()).request()
                withContext(Dispatchers.Main){
                    progressDialog.dismiss()
                    file.delete()
                }
            }
        }
    }
    private fun compressImage(): ByteArray? {
        file = File(snap.currentPhotoPath)
        var bitmap: Bitmap? = null

        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
        } catch (e: IOException) { e.printStackTrace() }

        val stream = ByteArrayOutputStream()
        // write compressed bitmap to the output stream
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}