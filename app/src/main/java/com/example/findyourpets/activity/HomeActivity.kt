package com.example.findyourpets.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.findyourpets.R
import com.google.firebase.database.*
import com.example.findyourpets.`object`.User
import com.example.findyourpets.fragment.NewsFeed
import com.example.findyourpets.fragment.Profile
import com.example.findyourpets.fragment.Settings
import com.example.findyourpets.fragment.UploadPost
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var ref:DatabaseReference
    private lateinit var nuser:User

    private val newsFeed:Fragment= NewsFeed()
    val profile:Fragment= Profile()
    val uploadPost:Fragment= UploadPost()
    val settings:Fragment= Settings()
    val fManager:FragmentManager = supportFragmentManager
    var active:Fragment= newsFeed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ref=FirebaseDatabase.getInstance().reference
        val userEmail:String = intent.extras!!.getString("email")!!
        val userID:String= intent.extras!!.getString("ID")!!
        val userName:String= intent.extras!!.getString("name")!!
        val userActive:String= intent.extras!!.getString("isActive")!!
        val userAdmin:String= intent.extras!!.getString("isAdmin")!!
        val userCreatedDate:String= intent.extras!!.getString("createdDated")!!
        val userPhotoUri: String = intent.extras!!.getString("photoUri")!!
        //val userPhone:String=intent.extras!!.getString("phoneNumber")!!

        ref.child("Users/").child(userID).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                print("failed")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user: User? =p0.getValue(User::class.java)
                if (user==null){
                    nuser= User(userEmail, userName, userAdmin.toBoolean(), userCreatedDate, userActive.toBoolean(), "NA","NA","NA",userPhotoUri)
                    ref.child("Users/").child("$userID/").setValue(nuser)
                }
                else{
                    nuser=user
                }
            }
        })

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fManager.beginTransaction().add(R.id.fragment_container, profile, "profile").hide(profile).commit()
        fManager.beginTransaction().add(R.id.fragment_container, uploadPost, "upload post").hide(uploadPost).commit()
        fManager.beginTransaction().add(R.id.fragment_container, settings, "settings").hide(settings).commit()
        fManager.beginTransaction().add(R.id.fragment_container, newsFeed, "newsfeed").commit()
    }

    private val mOnNavigationItemSelectedListener=BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId){
            R.id.new_feed ->{
                fManager.beginTransaction().hide(active).show(newsFeed).commit()
                active=newsFeed
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile->{
                fManager.beginTransaction().hide(active).show(profile).commit()
                active=profile
                return@OnNavigationItemSelectedListener true
            }
            R.id.post->{
                fManager.beginTransaction().hide(active).show(uploadPost).commit()
                active=uploadPost
                return@OnNavigationItemSelectedListener true
            }
            R.id.settings->{
                fManager.beginTransaction().hide(active).show(settings).commit()
                active=settings
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }
}
