package ca.unb.mobiledev.weatherwizard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonLaunch = findViewById<Button>(R.id.buttonLaunch)
        val buttonSettings = findViewById<Button>(R.id.buttonSettings)

        buttonLaunch.setOnClickListener {
            Toast.makeText(this, "Launch clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LaunchActivity::class.java)
            startActivity(intent)
        }

        buttonSettings.setOnClickListener {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}