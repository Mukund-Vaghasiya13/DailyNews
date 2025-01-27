package com.example.dailynews.AppInterface



import com.example.dailynews.modle.NewsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @POST("api/v1/User/login")
    fun login(@Body param:Map<String,String>): Call<ResponseBody>

    @POST("api/v1/User/register")
    fun Signup(@Body param:Map<String,String>): Call<ResponseBody>

    @GET("top-headlines")
    fun loadArticles(@Query("category") category:String,@Query("apiKey") apiKey:String, @Header("User-Agent") userAgent: String = "DailyNews/1.0 (Android)"):Call<NewsResponse>

    @GET("everything")
    fun search(
        @Query("q") q: String,
        @Query("apiKey") apiKey: String,
        @Query("sortBy") sortBy: String = "popularity",
        @Query("from") from: String
    ): Call<NewsResponse>


}