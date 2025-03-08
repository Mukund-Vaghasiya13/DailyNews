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

    // Optimized Retrofit instance to avoid multiple creations
    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        val appbar = findViewById<MaterialToolbar>(R.id.homebar)
        setSupportActionBar(appbar)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this) // Ensure layout manager is set before using RecyclerView

        // Load initial category news
        NetworkCall("business")

        val tabbar = findViewById<TabLayout>(R.id.tabbar)
        tabbar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val category = tab?.text?.toString() ?: "general"
                NetworkCall(category) // Fetch news based on tab selection
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun NetworkCall(content: String) {
        val apiKey = "750a1ffc45194fe0922a46ad88c2be79" // TODO: Move this to a secure place

        val retroData = api.loadArticles(content, apiKey)
        retroData.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null || body.articles.isNullOrEmpty()) {
                        Toast.makeText(applicationContext, "No articles found", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val recycler = findViewById<RecyclerView>(R.id.recycler)
                    recycler.adapter = ListArticleAdpter(body.articles, this@HomeScreen) { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                } else {
                    Log.e("NetworkError", "Code: ${response.code()}, Message: ${response.message()}, ErrorBody: ${response.errorBody()?.string()}")
                    Toast.makeText(applicationContext, "Network Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homemenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(applicationContext, SearchActivity::class.java))
                true
            }
            R.id.Logout -> {
                val shared = getSharedPreferences("DailyNews", Context.MODE_PRIVATE)
                shared?.edit()?.remove("LoginToken")?.apply()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
