package com.example.dailynews

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynews.Adpter.ListArticleAdpter
import com.example.dailynews.AppInterface.ApiInterface
import com.example.dailynews.modle.NewsResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SearchActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var searchButton: MaterialButton
    private lateinit var recycler: RecyclerView
    private lateinit var errorLabel: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Initialize views
        editText = findViewById(R.id.search)
        searchButton = findViewById(R.id.searchBtn)
        recycler = findViewById(R.id.recycler)
        errorLabel = findViewById(R.id.errorLabel)


        // Set up search button click listener
        searchButton.setOnClickListener {
            val content = editText.text.toString().trim()
            if (content.isBlank()) {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            } else {
                performNetworkCall(content)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun performNetworkCall(content: String) {
        // Show progress bar
        recycler.visibility = View.GONE
        errorLabel.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val currentDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val call = retrofit.search(
            q = content,
            apiKey = "750a1ffc45194fe0922a46ad88c2be79",
            sortBy = "popularity",
            from = currentDate
        )

        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {

                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse?.articles?.isNotEmpty() == true) {
                        recycler.adapter = ListArticleAdpter(newsResponse.articles, this@SearchActivity) { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        }
                        recycler.layoutManager = LinearLayoutManager(this@SearchActivity)
                        recycler.visibility = View.VISIBLE
                    } else {
                        errorLabel.text = "No articles found for \"$content\". Please try a different search term."
                        errorLabel.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("SearchActivity", "Response Error: ${response.code()} - ${response.message()}")
                    errorLabel.text = "Failed to load articles. Please try again later."
                    errorLabel.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                errorLabel.text = t.message ?: "Unable to fetch articles. Please check your internet connection."
                errorLabel.visibility = View.VISIBLE
                Log.e("SearchActivity", "Network Call Failed: ${t.message}", t)
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}
