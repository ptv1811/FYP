package com.example.findyourpets.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.findyourpets.R
import com.google.firebase.database.*
import com.example.findyourpets.`object`.User
import com.example.findyourpets.fragment.NewsFeed
import com.example.findyourpets.fragment.Profile
import com.example.findyourpets.fragment.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.getValue

class HomeActivity : AppCompatActivity() {

    private lateinit var ref:DatabaseReference
    private var nuser:User = User()

    private val newsFeed:Fragment= NewsFeed()
    private val profile:Fragment= Profile()
    private val settings:Fragment= Settings()
    private val fManager:FragmentManager = supportFragmentManager
    private var active:Fragment= newsFeed

    var apiKey: String = "3ac99f1d28610dd2d009b40300762140354758e6"
    var appID : String = "208055a14a02152"
    var region: String = "us"
    var authKey: String = "8f6c81a29d471c4ee96f5c94da978cf999f30dfa"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region)
            .build()

        ref=FirebaseDatabase.getInstance().reference
        val userEmail:String = intent.extras!!.getString("email")!!
        val userID:String= intent.extras!!.getString("ID")!!
        val userName:String= intent.extras!!.getString("name")!!
        val userActive:String= intent.extras!!.getString("isActive")!!
        val userAdmin:String= intent.extras!!.getString("isAdmin")!!
        val userCreatedDate:String= intent.extras!!.getString("createdDated")!!
        val userPhotoUri: String = intent.extras!!.getString("photoUri")!!
        //val userPhone:String=intent.extras!!.getString("phoneNumber")!!

        CometChat.init(this,appID,appSettings, object: CometChat.CallbackListener<String>(){
            override fun onSuccess(p0: String?) {

            }

            override fun onError(p0: CometChatException?) {
            }

        })

        if (CometChat.getLoggedInUser() == null){
            CometChat.login(userID, authKey, object: CometChat.CallbackListener<com.cometchat.pro.models.User>(){
                override fun onSuccess(p0: com.cometchat.pro.models.User?) {
                }

                override fun onError(p0: CometChatException?) {

                }

            } )
        }




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
        //fManager.beginTransaction().add(R.id.fragment_container, uploadPost, "upload post").hide(uploadPost).commit()
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
                val intent = Intent(this, UploadPostActivity::class.java)
                startActivity(intent)
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
