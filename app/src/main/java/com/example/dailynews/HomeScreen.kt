package com.example.dailynews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynews.Adpter.ListArticleAdpter
import com.example.dailynews.modle.NewsResponse
import com.example.dailynews.AppInterface.ApiInterface
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeScreen : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)
        val appbar = findViewById<MaterialToolbar>(R.id.homebar)
        setSupportActionBar(appbar)
        NetworkCall("business")
        val tabbar = findViewById<TabLayout>(R.id.tabbar)
        tabbar.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val a = tab?.text?.toString() ?: "Default Title"
                NetworkCall(a)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }
    fun NetworkCall(content:String){
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retroData = retrofitBuilder.loadArticles(content, "750a1ffc45194fe0922a46ad88c2be79")
        retroData.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        val recycler = findViewById<RecyclerView>(R.id.recycler)
                        recycler.adapter = ListArticleAdpter(it.articles, this@HomeScreen){ url ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData(Uri.parse(url))
                            startActivity(intent)
                        }
                        val layoutManager = LinearLayoutManager(this@HomeScreen)
                        layoutManager.reverseLayout = false
                        recycler.layoutManager = layoutManager
                    }
                }else{
                    if (!response.isSuccessful) {
                        Log.e("NetworkError", "Code: ${response.code()}, Message: ${response.message()}, ErrorBody: ${response.errorBody()?.string()}")
                        Toast.makeText(applicationContext, "Network Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.homemenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.search -> {
               val intent = Intent(applicationContext,SearchActivity::class.java)
                startActivity(intent)
               true
            }
            R.id.Logout -> {
                val shared = getSharedPreferences("DailyNews", Context.MODE_PRIVATE)
                val edit = shared.edit()
                edit.remove("LoginToken")
                edit.apply()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }else->{
                 super.onOptionsItemSelected(item)
            }
        }
    }
}