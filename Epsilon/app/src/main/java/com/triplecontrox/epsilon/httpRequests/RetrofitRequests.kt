package com.triplecontrox.epsilon.httpRequests

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.site.MaintenanceActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class RetrofitRequests(private val context: Context, private val username: String) {
    // get the name of the user stored in shared preferences
    private val db = AppDatabase.getInstance(context).maintenanceDao()
    private var interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    fun uploadImage(imagePath: String, progressDialog: Dialog, table: String, site: Maintenance){
        // get the file through it's part name
        val file = File(imagePath)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        // MultipartBody.Part is used to send also the actual file name
        val photo = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), username)
        val directory = RequestBody.create(MediaType.parse("text/plain"), table)

        // make a retrofit object to prepare http request
        val retrofit = Retrofit.Builder()
            .baseUrl("http://hr.watershedcorporation.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val httpRequests = retrofit.create(HttpRequests::class.java)
        val myRes = httpRequests.upload(name, directory, photo)

        myRes.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                val serverResponse = Gson().toJson(response.body()?.string()) as String

                // if form was successfully uploads then start new activity
                if (serverResponse.isNotEmpty()) {
                    GlobalScope.launch{
                        withContext(Dispatchers.IO){
                            db.updateStatus(site.SiteName, site.Equipment_Code, site.Capacity, site.Equipment_Id)
                            file.delete()
                        }
                    }

                    val intent = Intent(context, MaintenanceActivity::class.java)
                    intent.putExtra("SiteName", site.SiteName)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }

                // else inform user of the unsuccessful upload
                else
                    Toast.makeText(context, "Couldn't upload the photo. Please retry",
                        Toast.LENGTH_LONG).show()
            }

            // if the network is poor, inform user to turn on their internet
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Please check your internet connection and retry!",
                    Toast.LENGTH_LONG).show()
            }
        })
    }
}