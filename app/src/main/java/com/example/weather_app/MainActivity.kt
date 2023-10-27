package com.example.weather_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.example.weather_app.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchWeatherData("kochi")
        searchCity()
    }

    private fun searchCity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit =Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val responce = retrofit.getWeatherData(cityName,"Enetr your Api Key","metric")
        responce.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responceBody =response.body()
                if (response.isSuccessful && responceBody != null){
                    val temparatures = responceBody.main.temp.toString()
                    val humidity = responceBody.main.humidity.toString()
                    val windSpeed = responceBody.wind.speed
                    val sunRise = responceBody.sys.sunrise.toLong()
                    val sunSet = responceBody.sys.sunrise.toLong()
                    val seaLeavel = responceBody.main.pressure
                    val condition = responceBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responceBody.main.temp_max
                    val minTemp = responceBody.main.temp_min

                    binding.temperature.text = "$temparatures"
                    binding.weather.text = condition
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunSet.text = "${times(sunSet)}"
                    binding.sea.text = "$seaLeavel hpa"
                    binding.conditions.text = condition
                    binding.day.text =dayName(System.currentTimeMillis())
                        binding.date.text =date()
                        binding.cityname.text = "$cityName"
                   // Log.d("TAG", "onResponse: $temparature")
                    cngImgAccordingToWtrCondition(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        } )

    }

    private fun cngImgAccordingToWtrCondition(conditions : String) {
        when(conditions){
            "Clear Sky", "Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimatiomView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds", "Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimatiomView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Rain","Drizzle","Moderate Rain" ,"Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimatiomView.setAnimation(R.raw.rain)
            }
            "Light Snow","Snow", "Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimatiomView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimatiomView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimatiomView.playAnimation()
    }

    fun dayName(timestamp: Long):String{
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }
    fun date(): String{
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    } fun time(timestamp: Long): String{
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format((Date(timestamp*1000)))
    } fun times(timestamp: Long): String{
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }
}
