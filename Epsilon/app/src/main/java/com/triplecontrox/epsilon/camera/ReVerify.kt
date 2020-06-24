package com.triplecontrox.epsilon.camera

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.dataClasses.WebRequestBody
import com.triplecontrox.epsilon.httpRequests.AWSRecognition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

// dispatches user's request to server after he/she has been verified successfully
class ReVerify : AppCompatActivity() {
    private lateinit var file: File
    private lateinit var progress: Dialog
    private var body = HashMap<String, String>()
    private val snap = CameraVerify(this, 101)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        val modelIntent: WebRequestBody = intent.getParcelableExtra("requestBody")
        val button: Button = findViewById(R.id.btnCamera)
        button.setOnClickListener { snap.takePicture() }
        snap.takePicture()

        body["Id"] = modelIntent.Id.toString()
        body["Assignee"] = modelIntent.assignee!!
        body["SiteName"] = modelIntent.siteName!!
        body["table"] = "RE_VERIFY"
        body["P"] = "XemPl1fy"
        body["url"] = modelIntent.url!!
        body["Address"] = modelIntent.location?.provider!!
        body["column"] = modelIntent.verifyCount.toString()
        body["Latitude"] = modelIntent.location.latitude.toString()
        body["Longitude"] = modelIntent.location.longitude.toString()

        // prepare progress bar
        progress = AlertDialog.Builder(this).setView(R.layout.progress).create()
        progress.setCancelable(false)
    }
    override fun onActivityResult(requestCode: Int, resultStatus: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultStatus, data)
        if (requestCode == 101 && resultStatus == RESULT_OK) {
            progress.show()
            GlobalScope.launch {
                AWSRecognition(applicationContext, body, compressImage()).request()
                progress.dismiss()
            }
            file.delete()
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
