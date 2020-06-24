package com.triplecontrox.epsilon.httpRequests

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.*


interface HttpRequests {

    @Multipart
    @POST("/epsilon-data/upload.php")
    fun upload(
        @Part("user") username: RequestBody,
        @Part("table") table: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}