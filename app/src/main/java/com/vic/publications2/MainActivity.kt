package com.vic.publications2

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vic.publications2.adapters.RecyclerAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://beira.pt"

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {
    lateinit var countDownTimer: CountDownTimer
    private var seconds = 3L

    private var titlesList = mutableListOf<String>()
    private var datesList = mutableListOf<String>()
    private var imagesList = mutableListOf<String>()
    private var linksList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeAPIRequest()
    }

    private fun fadeInFromBlack() {
        val vBlackScreen = findViewById<View>(R.id.v_blackScreen)
        vBlackScreen.animate().apply {
            alpha(0f)
            duration = 3000
        }.start()
    }

    private fun setUpRecyclerView() {
        val rvRecyclerView = findViewById<RecyclerView>(R.id.rv_recyclerView)
        rvRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        rvRecyclerView.adapter = RecyclerAdapter(titlesList, datesList, imagesList, linksList)
    }

    private fun addToList(title: String, date: String, image: String, link: String) {
        titlesList.add(title)
        datesList.add(date)
        imagesList.add(image)
        linksList.add(link)
    }

    private fun makeAPIRequest() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getNews()

                for (article in response) {
                    Log.d("MainActivity", "Result = $article")
                    addToList(article.title, article.date, article.image, article.link)
                }

                withContext(Dispatchers.Main) {
                    setUpRecyclerView()
                    fadeInFromBlack()
                    progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                Log.d("MainActivity", e.toString())

                withContext(Dispatchers.Main) {
                    attemptRequestAgain(seconds)
                }
            }
        }
    }

    private fun attemptRequestAgain(seconds: Long) {
        countDownTimer = object: CountDownTimer(seconds*1000, 1000) {
            val tvNoInternetCountDown = findViewById<TextView>(R.id.tv_noInternetCountDown)

            override fun onFinish() {
                makeAPIRequest()
                countDownTimer.cancel()
                tvNoInternetCountDown.visibility = View.GONE
                this@MainActivity.seconds+=3
            }
            override fun onTick(millisUntilFinished: Long) {
                tvNoInternetCountDown.visibility = View.VISIBLE
                tvNoInternetCountDown.text = getString(R.string.Could_not_connect, "${millisUntilFinished/1000}")
                Log.d("MainActivity", "Could not connect, trying again in ${millisUntilFinished/1000} seconds")
            }
        }
        countDownTimer.start()
    }
}