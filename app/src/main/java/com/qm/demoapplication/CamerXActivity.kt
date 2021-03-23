package com.qm.demoapplication

import android.Manifest
import android.app.ActivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.security.Permission
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*

class CamerXActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var preview: Preview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camer_x)

        checkMyPermission()

        findViewById<View>(R.id.btn_take_photo).setOnClickListener {
            takePhoto()
        }
    }

    private fun checkMyPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),101)
    }

    fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

         var  hasPermission=false
         if (requestCode==101){
              for ((i1,i2) in grantResults.withIndex()){
                   if (i2==0){
                       hasPermission=true
                   }
              }
         }

        if(hasPermission){
            startCamera()
        }else{
            finish()
            Toast.makeText(this,"无权限",Toast.LENGTH_SHORT).show()
        }
    }


    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        imageCapture = ImageCapture.Builder().build()

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
           preview = Preview.Builder().build()

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture)
                preview?.setSurfaceProvider(findViewById<PreviewView>(R.id.pvView).createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                Log.e("CamerX", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }



    private fun takePhoto(){
         imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(getOutputDirectory(), SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        imageCapture?.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CamerX", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("CamerX", msg)
                }
            })
    }
}