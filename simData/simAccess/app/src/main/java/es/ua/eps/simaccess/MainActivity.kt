package es.ua.eps.simaccess

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat

private const val PERMISSION_CODE = 0
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val textView: TextView = findViewById(R.id.deviceInfo)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE)
                return
            }
            val phoneNumber = telephonyManager.line1Number
            val imei = telephonyManager.imei
            val simSerialNumber = telephonyManager.simSerialNumber
            val simOperator = telephonyManager.simOperator
            val simOperatorName = telephonyManager.simOperatorName
            val simIMSI = telephonyManager.subscriberId
            val networkOperatiorName = telephonyManager.networkOperatorName
            val networkOperator = telephonyManager.networkOperator
            val isoSim = telephonyManager.simCountryIso
            val isoNet = telephonyManager.networkCountryIso
            val imeiVersion = telephonyManager.deviceSoftwareVersion
            val ansNumber = telephonyManager.voiceMailNumber
            val roaming = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.isDataRoamingEnabled
            } else {
                null
            }
            val data = telephonyManager.isDataEnabled
            var phoneType = ""
            when (telephonyManager.phoneType) {
                TelephonyManager.PHONE_TYPE_CDMA -> phoneType = "CDMA"
                TelephonyManager.PHONE_TYPE_GSM -> phoneType = "GSM"
                TelephonyManager.PHONE_TYPE_NONE -> phoneType = "NONE"
            }
            var gsmCellLocation = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gsmCellLocation = telephonyManager.allCellInfo.toString()

            }

            textView.text = String.format(
                    getString(R.string.result),
                    phoneNumber,
                    phoneType,
                    data,
                    imei,
                    simSerialNumber,
                    simOperator,
                    simOperatorName,
                    networkOperator,
                    networkOperatiorName,
                    isoSim,
                    isoNet,
                    ansNumber,
                    roaming,
                    simIMSI,
                    imeiVersion,
                    gsmCellLocation
            )
        }
        else {
            Toast.makeText(this, getText(R.string.badVersion), Toast.LENGTH_LONG).show()
        }
    }

    private fun launchSettings() {
        Toast.makeText(this, getText(R.string.requestReadStatePermissions), Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", this.packageName, null)
        startActivity(intent)
        finish()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE)
                }
                else {
                    launchSettings()
                }
            }
            else {
                recreate()
            }
        }
    }
}