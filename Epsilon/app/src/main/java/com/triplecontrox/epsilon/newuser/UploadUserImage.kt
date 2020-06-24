package com.triplecontrox.epsilon.newuser

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.camera.CameraVerify
import com.triplecontrox.epsilon.S3Bucket
import kotlinx.coroutines.*
import java.io.*

class UploadUserImage : AppCompatActivity() {
    private val maxFaces = 2
    private lateinit var spinner: Spinner
    private lateinit var progress: Dialog
    private lateinit var snackBar: Snackbar
    private val dropList =  mutableListOf<String>()
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private val snap = CameraVerify(this, 101)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
        setContentView(R.layout.activity_upload_user_image)

        val button: Button = findViewById(R.id.btnCamera)
        button.setOnClickListener { snap.takePicture() }

        spinner = findViewById(R.id.spinner)

        spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item1, dropList)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // deactivate button when no spinner item is selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { button.isEnabled = false }

            // activate button when an item other than the first one is selected
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                view.isSelected = true
                button.isEnabled = position != 0
            }
        }

        snackBar =
            Snackbar.make(findViewById(android.R.id.content), "TOO MANY OR NO FACE FOUND",
            Snackbar.LENGTH_INDEFINITE).setAction("X") { snackBar.dismiss() }

        // prepare progress bar
        progress =
            AlertDialog.Builder(this).setView(R.layout.progress).setCancelable(false).create()
        urlRequest()
    }
    override fun onActivityResult(requestCode: Int, resultStatus: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultStatus, data)
        if (requestCode == 101 && resultStatus == RESULT_OK) {
            progress.show()
            GlobalScope.launch(Dispatchers.IO) {
//                val image = compressImage()
//                if (image == null){
//                    snackBar.setText("IMAGE WASN'T FOUND! TRY AGAIN").show()
//                    return@launch
//                }
//                val facesDetected =
//                    FaceDetector(image.width, image.height, maxFaces).findFaces(image, arrayOfNulls(maxFaces))

//                Log.d("faces detected", facesDetected.toString())
                // if a face is detected in the photo then upload the image to s3bucket
//                when(facesDetected){
//                    1 -> {
                val bytes = compressImage()
                if (bytes != null) {
                    S3Bucket.upload(applicationContext, spinner.selectedItem.toString(),
                        ByteArrayInputStream(bytes))
                }
//                    }
//                    else -> snackBar.show()
//                }
                withContext(Dispatchers.Main){ progress.dismiss() }
            }
        }
    }

    private fun compressImage(): ByteArray? {
        val stream = ByteArrayOutputStream()
        val options = BitmapFactory.Options()

        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inJustDecodeBounds = false
        options.outWidth = 900
        options.outHeight = 900

        //decode image from file path and compress to an output stream
        val bitmap = BitmapFactory.decodeFile(snap.currentPhotoPath, options)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        return stream.toByteArray()
    }
    private fun urlRequest(){
        val url = "http://hr.watershedcorporation.com/epsilon-data/staff.php"
        //String Request initialized
        val volleyRequest = object : JsonArrayRequest(Method.GET, url,null,
            Response.Listener { response ->
            if (!dropList.isNullOrEmpty()) dropList.clear()
                dropList.add("NO USERNAME SELECTED")
            for( i in 0 until response.length()){
                dropList.add(response.getString(i))
                spinnerAdapter.notifyDataSetChanged()
            }
        }, Response.ErrorListener { error ->
            if (error.toString() == "com.android.volley.TimeoutError")
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        }) {}

    volleyRequest.retryPolicy =
        DefaultRetryPolicy(60000, 4, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    volleyRequest.setShouldCache(true)
    Volley.newRequestQueue(applicationContext).add(volleyRequest)
    }
}