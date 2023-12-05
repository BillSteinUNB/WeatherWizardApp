package ca.unb.mobiledev.weatherwizard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ca.unb.mobiledev.weatherwizard.ui.theme.WeatherWizardTheme
import com.google.android.gms.location.FusedLocationProviderClient
import android.widget.TextView
import com.google.android.gms.location.LocationServices
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



// Use MyTypography in your theme


class LaunchActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // State for weather text
        val weatherText = mutableStateOf("Fetching weather...")
        val hardcodedWeatherData = " 15 degrees Celsius"

        setContent {
            WeatherWizardTheme {
                WeatherScreen(city ="fredericton", temperature = hardcodedWeatherData )
            }
        }

        getLocationAndWeather(weatherText)
    }

    private fun getLocationAndWeather(weatherText: MutableState<String>) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val weatherUrl = "https://api.weatherbit.io/v2.0/current?lat=${location.latitude}&lon=${location.longitude}&key=d48fe7c323934ed0bb8c26854b3d9e0a"
                getCurrentWeather(weatherUrl, weatherText)
            }
        }
    }

    private fun getCurrentWeather(url: String, weatherText: MutableState<String>) {
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(Request.Method.GET, url, { response ->
            val obj = JSONObject(response)
            val arr = obj.getJSONArray("data")
            val obj2 = arr.getJSONObject(0)
            weatherText.value = "The temperature in ${obj2.getString("city_name")} is ${obj2.getString("temp")} degrees Celsius"
        }, { weatherText.value = "Error fetching weather data" })
        queue.add(stringReq)
    }

    @Composable
    fun WeatherScreen(city: String, temperature: String) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = city,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Temperature: $temperature",
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun WeatherScreenPreview() {
        WeatherWizardTheme {
            WeatherScreen(city = "Fredericton", temperature = "15 degrees Celsius")
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(text = "Hello $name!", modifier = modifier)
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        WeatherWizardTheme {
            Greeting("Android")
        }
    }
}
