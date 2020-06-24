package com.triplecontrox.epsilon.camera

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

// this class is specifically for taking pictures and getting back the picture from the storage
class CameraVerify(private val context: Context, private val REQUEST_IMAGE_CAPTURE: Int) {
    var currentPhotoPath: String = ""

    fun takePicture() {
        val picIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (picIntent.resolveActivity(context.packageManager) != null) {
            // Create the File where the photo should be saved
            val photoFile = createImageFile(context)

            // Continue only if the File was successfully created
            val photoURI =
                FileProvider.getUriForFile(context, "com.triplecontrox.epsilon.fileprovider",
                    photoFile)

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                picIntent.clipData = ClipData.newRawUri("", photoURI)

                picIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            (context as Activity).startActivityForResult(picIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timestamp = System.currentTimeMillis()
        val imageFileName = "img_$timestamp"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Save a file path for use with ACTION_VIEW intents
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = image.absolutePath
        return image
    }
}