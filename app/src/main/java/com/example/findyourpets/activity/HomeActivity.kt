package com.example.findyourpets.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findyourpets.R
import com.google.firebase.database.*
import com.example.findyourpets.`object`.User

class HomeActivity : AppCompatActivity() {

    private lateinit var ref:DatabaseReference
    private lateinit var nuser:User

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
        //val userPhone:String=intent.extras!!.getString("phoneNumber")!!

        ref.child("Users/").child(userID).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                print("failed")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user: User? =p0.getValue(User::class.java)
                if (user==null){
                    nuser= User(userEmail, userName, userAdmin.toBoolean(), userCreatedDate, userActive.toBoolean(), "NA","NA","NA")
                    ref.child("Users/").child("$userID/").setValue(nuser)
                }
                else{
                    nuser=user
                }
            }
        })
    }
}
