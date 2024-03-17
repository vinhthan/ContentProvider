package com.example.contentprovider

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var edt: EditText
    private lateinit var btn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edt = findViewById(R.id.edt_android_id)
        btn = findViewById(R.id.btn_add)
        btn.setOnClickListener {
            val values = ContentValues()
            values.put(AndroidIDProvider.VALUE, edt.text.toString())
            val uri = contentResolver.insert(AndroidIDProvider.CONTENT_URI, values)
            Toast.makeText(baseContext, uri.toString(), Toast.LENGTH_LONG).show()
        }


    }


}