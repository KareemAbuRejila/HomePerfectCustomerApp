package com.codeshot.home_perfect.remote

import com.codeshot.home_perfect.models.DataMessage
import com.codeshot.home_perfect.models.FCMResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAGU0hNUw:APA91bG6kqGYri5lLUo3qliHGE4nhC08Xn0RgVGjHIQo8bmo4l_LjyPg-UZhpO7CU4uFbweTJboRjk3UDXn2ND1xe0dWUTU6yyX0W0AqH0zDDrklwnf-LxNz6WK71Ly0vYxwxeMqkOXp"
    )
    @POST("fcm/send")
    fun sendMessage(@Body body: DataMessage?): Call<FCMResponse>
}