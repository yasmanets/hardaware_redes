package es.ua.eps.practice1_gps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationPermissionHelper {
    private const val REQUEST_PERMISSION_CODE = 0
    private const val LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION
    private const val COARSE_PERMISSION = android.Manifest.permission.ACCESS_COARSE_LOCATION

    fun hasPermissions(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, COARSE_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(LOCATION_PERMISSION, COARSE_PERMISSION), REQUEST_PERMISSION_CODE)
    }

    fun shouldShowRequestPermissionsRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, LOCATION_PERMISSION)
    }

    fun launchSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}
class MainActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var location: TextView? = null
    private var btnGetLocation: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        location = findViewById(R.id.location)
        btnGetLocation = findViewById(R.id.getLocation)
    }

    public override fun onStart() {
        super.onStart()
        if (!LocationPermissionHelper.hasPermissions(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LocationPermissionHelper.requestPermissions(this)
                return
            }
            else {
                Toast.makeText(this, getText(R.string.badVersion), Toast.LENGTH_LONG).show()
                finish()
            }
        }
        else {
            btnGetLocation?.setOnClickListener {
                getLastLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                location?.text = getString(R.string.location, (lastLocation)!!.longitude, (lastLocation)!!.latitude)
            }
            else {

                Log.d("M;ain", "getLastLocation:exception", task.exception)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!LocationPermissionHelper.hasPermissions(this)) {
            Toast.makeText(this, getText(R.string.requestLocationPermissions), Toast.LENGTH_LONG).show()
            if (!LocationPermissionHelper.shouldShowRequestPermissionsRationale(this)) {
                LocationPermissionHelper.launchSettings(this)
            }
            finish()
        }
        recreate()
    }
}