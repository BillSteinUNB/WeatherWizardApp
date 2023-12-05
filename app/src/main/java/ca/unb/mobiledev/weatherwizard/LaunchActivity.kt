package ca.unb.mobiledev.weatherwizard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import ca.unb.mobiledev.weatherwizard.ui.theme.WeatherWizardTheme
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.animateContentSize




val weatherfont = FontFamily(
    Font(R.font.weatherfont, FontWeight.Normal)
)

val h4TextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 35.sp,
    fontFamily= weatherfont,
    letterSpacing = 0.sp
)

val h6TextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 25.sp,
    fontFamily= weatherfont,
    letterSpacing = 0.15.sp
)



class LaunchActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // State for weather text
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // State for weather text
        val weatherText = mutableStateOf("Fetching weather...")

        setContent {
            WeatherWizardTheme {

                WeatherScreen(weatherText.value)
            }
        }

        getLocationAndWeather(weatherText)
    }

    private fun getLocationAndWeather(weatherText: MutableState<String>) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = 45.9636
                val longitude = -66.6431
                val weatherUrl =
                    "https://api.weatherbit.io/v2.0/current?lat=$latitude&lon=$longitude&key=d48fe7c323934ed0bb8c26854b3d9e0a"
                //val weatherUrl = "https://api.weatherbit.io/v2.0/current?lat=${location.latitude}&lon=${location.longitude}&key=d48fe7c323934ed0bb8c26854b3d9e0a"
                getCurrentWeather(weatherUrl, weatherText)
            } else {
                val latitude = 45.9636
                val longitude = -66.6431
                val weatherUrl =
                    "https://api.weatherbit.io/v2.0/current?lat=$latitude&lon=$longitude&key=d48fe7c323934ed0bb8c26854b3d9e0a"
                getCurrentWeather(weatherUrl, weatherText)
                //requestNewLocationData(weatherText)
            }

        }
    }

    private fun requestNewLocationData(weatherText: MutableState<String>) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                // Use the new location
                val weatherUrl =
                    "https://api.weatherbit.io/v2.0/current?lat=${location.latitude}&lon=${location.longitude}&key=d48fe7c323934ed0bb8c26854b3d9e0a"
                getCurrentWeather(weatherUrl, weatherText)
            }
        }, Looper.myLooper())
    }

    private fun getCurrentWeather(url: String, weatherText: MutableState<String>) {

        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(Request.Method.GET, url, { response ->
            val obj = JSONObject(response)
            val arr = obj.getJSONArray("data")
            val obj2 = arr.getJSONObject(0)
            weatherText.value =
                "The temperature in ${obj2.getString("city_name")} is ${obj2.getString("temp")} degrees Celsius"
        }, { weatherText.value = "Error fetching weather data" })
        queue.add(stringReq)
    }

    @Composable
    fun WeatherScreen(weather: String) {
        // State to track the selected box
        var selectedBox by remember { mutableStateOf(-1) }

        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.sunshine),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )

            // Weather content
            if (selectedBox == -1) {
                // Normal view with city, temperature, and boxes
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Extract city and temperature from the weather string
                    val city = weather.substringAfter("in ").substringBefore(" is")
                    val temperature = weather.substringAfter("is ")
                        .substringBefore(" degrees") + "°C"

                    Text(
                        text = city,
                        style = h4TextStyle,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = temperature,
                        style = h6TextStyle
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(6) { index ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Gray)
                                .clickable { selectedBox = index },
                            contentAlignment = Alignment.Center
                        )
                        if (index < 5) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            } else {
                // View for the selected box
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .clickable { selectedBox = -1 }
                        .animateContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Text", fontSize = 24.sp, color = Color.White)
                }
            }
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


