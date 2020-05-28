package com.example.findyourpets.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findyourpets.R
import com.facebook.login.Login
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent= Intent(this, LoginActivity::class.java )
        startActivity(intent)
        finish()
    }
}
