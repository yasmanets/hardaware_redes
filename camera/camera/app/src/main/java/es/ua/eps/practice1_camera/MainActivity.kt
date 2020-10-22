package es.ua.eps.practice1_camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object CameraPermissionHelper {
    private const val CAMERA_PERMISSION_CODE = 0
    private const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA

    fun hasPermissions(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(CAMERA_PERMISSION), CAMERA_PERMISSION_CODE)
    }

    fun shouldShowRequestPermissionsRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)
    }

    fun launchSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}
const val REQUEST_IMAGE_CODE = 100

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!CameraPermissionHelper.hasPermissions(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraPermissionHelper.requestPermissions(this)
                return
            }
            else {
                Toast.makeText(this, getText(R.string.badVersion), Toast.LENGTH_LONG).show()
                finish()
            }

        }

        val btnCamera: Button = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val image: ImageView = findViewById(R.id.image)
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    image.setImageBitmap(imageBitmap)
                }
                else ->
                    Toast.makeText(this, getText(R.string.notImage), Toast.LENGTH_LONG).show()
            }
        }
        else {
            Toast.makeText(this, getText(R.string.notImage), Toast.LENGTH_LONG).show()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!CameraPermissionHelper.hasPermissions(this)) {
            Toast.makeText(this, getText(R.string.requestCameraPermissions), Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionsRationale(this)) {
                CameraPermissionHelper.launchSettings(this)
            }
            finish()
        }
        recreate()
    }
}
