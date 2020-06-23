package com.example.findyourpets.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.findyourpets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private var storage= Firebase.storage

    private var IMAGE_REQUEST: Int = 1
    private lateinit var avatarUri : Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.back_button)

        backButton.setOnClickListener{
            finish()
        }



    }
}