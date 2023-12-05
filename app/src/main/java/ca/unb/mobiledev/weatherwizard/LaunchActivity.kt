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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley






class LaunchActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textView: TextView
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        weatherTextView = findViewById(R.id.weatherTextView)

        getLocationAndWeather()
    }

    private fun getLocationAndWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val weatherUrl = "https://api.weatherbit.io/v2.0/current?lat=${location.latitude}&lon=${location.longitude}&key=YOUR_API_KEY"
                getCurrentWeather(weatherUrl)
            }
        }
    }

    private fun getCurrentWeather(url: String) {
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(Request.Method.GET, url, { response ->
            val obj = JSONObject(response)
            val arr = obj.getJSONArray("data")
            val obj2 = arr.getJSONObject(0)
            val weatherText = "The temperature in ${obj2.getString("city_name")} is ${obj2.getString("temp")} degrees Celsius"
            textView.text = weatherText
        }, { textView.text = "Error fetching weather data" })
        queue.add(stringReq)
    }
}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherWizardTheme {
        Greeting("Android")
    }
}