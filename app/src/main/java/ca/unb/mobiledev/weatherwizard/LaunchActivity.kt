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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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


val weatherCode = mutableStateOf("test")
val windInfo = mutableStateOf("test")

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
val icon = (R.drawable.t01d)
fun setIcon(weatherCode: String): Int{
    val icon = null
    return when (weatherCode.toIntOrNull()){
        200 -> R.drawable.t01d
        201 -> R.drawable.t02d
        202 -> R.drawable.t03d
        230 -> R.drawable.t04d
        231 -> R.drawable.t04d
        232 -> R.drawable.t04d
        233 -> R.drawable.t05d
        300 -> R.drawable.d01d
        301 -> R.drawable.d02d
        302 -> R.drawable.d03d
        500 -> R.drawable.r01d
        501 -> R.drawable.r02d
        502 -> R.drawable.r03d
        511 -> R.drawable.f01d
        520 -> R.drawable.r04d
        521 -> R.drawable.r05d
        522 -> R.drawable.r06d
        600 -> R.drawable.s01d
        601 -> R.drawable.s02d
        602 -> R.drawable.s03d
        610 -> R.drawable.s04d
        611 -> R.drawable.s05d
        612 -> R.drawable.s05d
        621 -> R.drawable.s01d
        622 -> R.drawable.s02d
        623 -> R.drawable.s06d
        700 -> R.drawable.a01d
        711 -> R.drawable.a02d
        721 -> R.drawable.a03d
        731 -> R.drawable.a04d
        741 -> R.drawable.a05d
        751 -> R.drawable.a06d
        800 -> R.drawable.c01d
        801 -> R.drawable.c02d
        802 -> R.drawable.c02d
        803 -> R.drawable.c03d
        804 -> R.drawable.c04d
        else -> {
            R.drawable.u00d
        }
    }

}




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

                val weatherInfo = weatherText.value.split(": ")
                val city = weatherInfo.getOrNull(0) ?: "Unknown"
                val temperature = weatherInfo.getOrNull(1) ?: "N/A"
                val weatherCode = weatherCode.value

                WeatherScreen(city, temperature, weatherCode)
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
                getCurrentWeather(weatherUrl, weatherText, weatherCode, windInfo)
            } else {
                val latitude = 45.9636
                val longitude = -66.6431
                val weatherUrl =
                    "https://api.weatherbit.io/v2.0/current?lat=$latitude&lon=$longitude&key=d48fe7c323934ed0bb8c26854b3d9e0a"
                getCurrentWeather(weatherUrl, weatherText, weatherCode, windInfo)

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
                getCurrentWeather(weatherUrl, weatherText, weatherCode, windInfo)
            }
        }, Looper.myLooper())
    }

    private fun getCurrentWeather(url: String, weatherText: MutableState<String>, weatherCode: MutableState<String>, windInfo: MutableState<String>) {
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(Request.Method.GET, url, { response ->
            val obj = JSONObject(response)
            val arr = obj.getJSONArray("data")
            val obj2 = arr.getJSONObject(0)
            val city = obj2.getString("city_name")
            val temp = obj2.getString("temp")
            val code = obj2.getString("weather").let { JSONObject(it).getString("code") }
            val windSpeed = obj2.getString("wind_spd")
            val windDir = obj2.getString("wind_cdir_full")
            weatherText.value = "$city: $temp°C"
            weatherCode.value = code
            windInfo.value = "Wind: $windSpeed m/s, $windDir"
        }, {
            weatherText.value = "Error fetching weather data"
            weatherCode.value = "" // Set to default or error code
            windInfo.value = "Wind data not available"
        })
        queue.add(stringReq)
    }



    @Composable
    fun WeatherScreen(city: String, temperature: String, weatherCode: String) {
        val weatherIconId = setIcon(weatherCode) ?: R.drawable.a06d // Fallback icon

        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.sunshine),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )

            // Weather information
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = city, style = h4TextStyle, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = weatherIconId),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = temperature, style = h6TextStyle)
                }
            }

            // Boxes at the bottom
            val iconTextPairs = listOf(
                Pair(R.drawable.wardrobe, "Text1"),
                Pair(R.drawable.wardrobe, "Text2"),
                Pair(R.drawable.wardrobe, "Text3"),
                Pair(R.drawable.wardrobe, "Text4"),
                Pair(R.drawable.windspeed, windInfo.value), // Wind icon with wind information
                Pair(R.drawable.wardrobe, "Recommended Wardrobe!")
            )
            var selectedBox by remember { mutableStateOf<Int?>(null) }

            if (selectedBox == null) {
                // Normal view
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, bottom = 105.dp, end = 15.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    iconTextPairs.forEachIndexed { index, (iconResId, text) ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Blue.copy(alpha = 0.3f))
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { selectedBox = if (selectedBox == index) null else index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (index == 4 && selectedBox == index) {
                                // Display wind information for the wind icon
                                Text(text = windInfo.value, fontSize = 12.sp, color = Color.Black)
                            } else {
                                // Display icon for other boxes
                                Icon(
                                    painter = painterResource(id = iconResId),
                                    contentDescription = "Icon $index",
                                    tint = Color.Black
                                )
                            }
                        }
                        if (index < iconTextPairs.size - 1) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            } else {
                // View for the selected box
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .clickable { selectedBox = null }
                        .padding(50.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(top = 50.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = iconTextPairs[selectedBox!!].first),
                            contentDescription = "Selected Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            text = iconTextPairs[selectedBox!!].second,
                            fontSize = 28.sp,
                            color = Color.Black
                        )
                    }
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


