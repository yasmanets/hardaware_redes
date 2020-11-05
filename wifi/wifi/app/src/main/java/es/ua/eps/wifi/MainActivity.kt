package es.ua.eps.wifi

import android.app.ListActivity
import android.content.Context
import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.annotation.RequiresApi

val info: ArrayList<String> = arrayListOf()

class MainActivity : ListActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWifiInfo()
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, info.toTypedArray())

    }

    private fun getWifiInfo() {
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo = wifi.connectionInfo
        val dhcpInfo: DhcpInfo = wifi.dhcpInfo
        val frequency =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) wifiInfo.frequency.toString() else null
        val hidden = if (wifiInfo.hiddenSSID) getText(R.string.hiddenSSID) else getText(R.string.visibleSSID)
        info.add("SSID: ${wifiInfo.ssid}");
        info.add("BSSID: ${wifiInfo.bssid}")
        info.add("${getText(R.string.frequency)}: $frequency")
        info.add("${getText(R.string.wifiSpeed)}: ${wifiInfo.linkSpeed} Mbps")
        info.add("${getText(R.string.wifiStrength)}: ${wifiInfo.rssi} dBm")
        info.add("IP: ${wifiInfo.ipAddress}");
        info.add("${getText(R.string.netmask)}: ${dhcpInfo.netmask}")
        info.add("${getText(R.string.gateway)}: ${dhcpInfo.gateway}")
        info.add("${getText(R.string.dhcpServer)}: ${dhcpInfo.serverAddress}")
        info.add("DNS1: ${dhcpInfo.dns1}")
        info.add("DNS2: ${dhcpInfo.dns2}")
        info.add("DHCP lease: ${dhcpInfo.leaseDuration} (hh:mm:ss)")
        info.add("${getText(R.string.externalIP)}: ${dhcpInfo.ipAddress}")
        info.add("${getText(R.string.isHidden)}: $hidden")
        Log.d("Main", "" + info);
    }
}