package com.ipz_fuha.ipz_fuha

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

internal class MainActivity : ComponentActivity() {
    private var user_field: EditText? = null
    private var main_button: Button? = null
    private var result_weather: TextView? = null

    data class Weather(
        val description: String,
        val icon: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        user_field = findViewById(R.id.user_field)
        main_button = findViewById(R.id.main_button)
        result_weather = findViewById(R.id.result_weather)

        main_button?.setOnClickListener {
            val city = user_field?.text.toString().trim()
            if (city.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, R.string.no_user_input, Toast.LENGTH_LONG).show()
            } else {
                val key = "03a189da022dc2c71b850fc291329ab9"
                val url =
                    "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$key&units=metric&lang=ua"

                val request = Request.Builder().url(url).build()
                val client = OkHttpClient()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to fetch weather data!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call, response: Response) {
                        val responseData = response.body()?.string()
                        if (responseData != null) {
                            val gson = Gson()
                            val jsonObject = JSONObject(responseData)
                            val weatherArray = jsonObject.getJSONArray("weather")
                            val weatherObject = weatherArray.getJSONObject(0)
                            val weather = Weather(
                                weatherObject.getString("description"),
                                weatherObject.getString("icon")
                            )
                            val humidity = jsonObject.getJSONObject("main").getInt("humidity")
                            val weatherResponse = gson.fromJson(responseData, WeatherResponse::class.java)
                            data class WeatherData(
                                val weather: List<Weather>,
                                val name: String,
                            )
                            data class Weather(
                                val main: String,
                                val description: String,
                                val icon: String
                            )
                            data class Main(
                                val temp: Double,
                                val humidity: Int,
                            )

                            runOnUiThread {
                                result_weather?.text =
                                    "Temperature in ${weatherResponse.name}: ${weatherResponse.main.temp}°C\n" +
                                            "Зараз ${weather.description}\n" +
                                            "Вологість $humidity %"
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Failed to fetch weather data!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
        }
    }

    data class WeatherResponse(
        val name: String,
        val main: Main
    )

    data class Main(
        val temp: Double
    )
}

            @Suppress("DEPRECATION")
            @SuppressLint("StaticFieldLeak")
            fun doInBackground(vararg strings: String): String {
                    var connection: HttpURLConnection? = null
                    var reader: BufferedReader? = null
                    return try {
                        val url = URL(strings[0])
                        connection = url.openConnection() as HttpURLConnection
                        connection.connect()
                        val stream = connection.inputStream
                        reader = BufferedReader(InputStreamReader(stream))
                        val buffer = StringBuffer()
                        var line: String? = ""
                        while (reader.readLine().also { line = it } != null) buffer.append(line)
                            .append("\n")
                        buffer.toString()
                    } catch (e: MalformedURLException) {
                        throw RuntimeException(e)
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    } finally {
                        connection?.disconnect()
                        try {
                            reader?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }


