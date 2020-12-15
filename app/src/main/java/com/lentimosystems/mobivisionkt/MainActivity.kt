package com.lentimosystems.mobivisionkt

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var btn_image: Button?= null
    var imageView: ImageView?= null
    var txt_image: TextView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_image = findViewById(R.id.btnImage)
        imageView = findViewById(R.id.image)
        txt_image = findViewById(R.id.txt_image)

        btn_image!!.setOnClickListener{
            view: View? -> chooseImage()
        }

    }

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),123)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Extract the image
        val bundle = data!!.extras
        val bitmap = bundle!!["data"] as Bitmap?
        imageView!!.setImageBitmap(bitmap)

        //Create a FirebaseVisionImage object from your image/bitmap.
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
        val firebaseVision = FirebaseVision.getInstance()
        val firebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer

        //Process the Image
        val task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
        task.addOnSuccessListener { firebaseVisionText: FirebaseVisionText ->
            //Set recognized text from image in our TextView
            val text = firebaseVisionText.text
            txt_image!!.text = text
        }
        task.addOnFailureListener { e: Exception -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() }
    }
}