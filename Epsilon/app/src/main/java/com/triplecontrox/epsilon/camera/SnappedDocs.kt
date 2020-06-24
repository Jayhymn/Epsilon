package com.triplecontrox.epsilon.camera

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.httpRequests.RetrofitRequests
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class SnappedDocs : AppCompatActivity() {
    private val code = 101
    private val snap = CameraVerify(this, code)
    private var imageView: ImageView? = null
    private lateinit var button: Button
    private lateinit var table: String
    private lateinit var progressDialog: Dialog
    private lateinit var maintenance: Maintenance
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapped_docs)

        maintenance = intent.extras!!.getParcelable("details")!!
        name = maintenance.Assignee.toString()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        table = sharedPreferences.getString("assignments", "SITE")!!

        imageView = findViewById(R.id.takenPic)
        button = findViewById(R.id.btnCamera)
        button.setOnClickListener { snap.takePicture() }

        val builder =  AlertDialog.Builder(this)
        builder.setView(R.layout.progress)

        // prepare progress bar
        progressDialog = builder.create()
        progressDialog.setCancelable(false)
    }
    override fun onActivityResult(requestCode: Int, resultStatus: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultStatus, data)
        if (requestCode == code && resultStatus == RESULT_OK) {
            compressImage()
            val retrofitRequests  = RetrofitRequests(this, name)
            // create an object to send multipart request to external php server
            retrofitRequests.uploadImage(snap.currentPhotoPath, progressDialog, table, maintenance)
            progressDialog.show()
        }
    }
    private fun compressImage() {
        val file = File(snap.currentPhotoPath)
        var bitmap: Bitmap? = null

        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
        } catch (e: IOException) { e.printStackTrace() }

        imageView!!.setImageBitmap(bitmap)

        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }
}
