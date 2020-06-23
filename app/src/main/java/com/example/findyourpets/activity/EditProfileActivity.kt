package com.example.findyourpets.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findyourpets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EditProfileActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)



    }
}