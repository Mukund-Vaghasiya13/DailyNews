package com.example.dailynews
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dailynews.AppInterface.ApiInterface
import com.example.dailynews.modle.AuthModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserLoginOrNot()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val toolBarTB: MaterialToolbar = findViewById<MaterialToolbar>(R.id.appBar)
        setSupportActionBar(toolBarTB)
        var signUpButton = findViewById<MaterialButton>(R.id.signup)
        signUpButton.setOnClickListener{
            val intent = Intent(applicationContext,SignupScreen::class.java)
            startActivity(intent)
        }
        val eoufield = findViewById<TextInputEditText>(R.id.eouname)
        val passwordfield = findViewById<TextInputEditText>(R.id.password)

        var loginButton = findViewById<MaterialButton>(R.id.Login)
        loginButton.setOnClickListener{

            if (eoufield.text!!.isNotEmpty() && passwordfield.text!!.isNotEmpty()) {
                val loginParam = mapOf(
                    "usernameOrEmail" to eoufield.text.toString(),
                    "password" to passwordfield.text.toString()
                )
                LoginCall(loginParam)
            } else {
                // Handle the case when either field is empty
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }


//            val intent = Intent(applicationContext,HomeScreen::class.java)
//            startActivity(intent)
        }
    }

    private fun LoginCall(param:Map<String,String>){
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://sharerecipy-backend.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val  retrodata = retrofitBuilder.login(param)
        retrodata.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val statusCode = response.code()
                if(statusCode == 201){
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val jsonString = responseBody.string()
                        val actualData = Gson().fromJson(jsonString, AuthModel::class.java)
                        val sharedPF = getSharedPreferences("DailyNews", Context.MODE_PRIVATE)
                        var editorsh = sharedPF.edit()
                        editorsh.putString("LoginToken",actualData.Logintoken)
                        editorsh.apply()
                        val intent = Intent(applicationContext,HomeScreen::class.java)
                        startActivity(intent)
                        finish()
                        // Add Data using Editorsh
//                        Toast.makeText(applicationContext, actualData.Logintoken, Toast.LENGTH_LONG).show()
                    } else {
                        // Handle the case where the response body is null
                        Toast.makeText(applicationContext, "Sorry, something went wrong. Please try again later.", Toast.LENGTH_LONG).show()
                    }
                }else{
                    // Handle Server other Status Code MYError
                    Toast.makeText(applicationContext,statusCode.toString(),Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "MYError: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun checkUserLoginOrNot(){
        val sharedPF = getSharedPreferences("DailyNews", Context.MODE_PRIVATE)
        var token = sharedPF.getString("LoginToken",null)
        Log.d("SharedPreferences", "Token: $token") // Log the token value
        if (token != null) {
            Log.d("Navigation", "Token found, navigating to HomeScreen")
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.d("Navigation", "No token found, staying on MainActivity")
        }
    }
}