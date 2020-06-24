package com.triplecontrox.epsilon

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File

object S3Bucket {
    private val amazonAWSCredentials = object : AWSCredentials {
        override fun getAWSAccessKeyId() = "AKIA5UDSNLC3553DVQUR"
        override fun getAWSSecretKey() = "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"
    }

    @Throws(Exception::class)
    suspend fun upload(context:Context, keyName: String, fileInputStream: ByteArrayInputStream) {
        withContext(Dispatchers.IO){
            val metadata =
                ObjectMetadata().apply { contentType = "image/jpg" }
            try {
                val client = AmazonS3Client(amazonAWSCredentials, Region.getRegion(Regions.US_EAST_2))
                    client.putObject("3controx-collection", "$keyName.jpg", fileInputStream,
                        metadata)

                showToast(context,"upload successful!")
            }
            catch (e: AmazonClientException) { showToast(context, e.toString()) }
            catch (e:AmazonServiceException) { showToast(context, e.toString()) }
        }
    }

    private suspend fun showToast(context: Context, text: String){
        withContext(Dispatchers.Main){
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }
    }
}