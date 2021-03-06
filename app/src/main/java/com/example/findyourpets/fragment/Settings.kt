package com.example.findyourpets.fragment

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.findyourpets.R
import com.example.findyourpets.`object`.MyAdapter
import com.example.findyourpets.`object`.User
import com.example.findyourpets.activity.EditProfileActivity
import com.example.findyourpets.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

class Settings : Fragment() {
    private lateinit var user:FirebaseUser
    private lateinit var mAuth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var  listSettings: ListView
    private lateinit var avatarView : CircularImageView
    private val picasso = Picasso.get()
    private lateinit var ref:DatabaseReference
    private val optionsTitle: Array<String> = arrayOf("Edit Profile","Push Notification","Help Center", "Sign Out")

    var apiKey: String = "3ac99f1d28610dd2d009b40300762140354758e6"
    var appID : String = "208055a14a02152"
    var region: String = "us"
    var authKey: String = "8f6c81a29d471c4ee96f5c94da978cf999f30dfa"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_settings, container, false)

        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region)
            .build()

        database = Firebase.database.reference
        avatarView = rootView.findViewById(R.id.avatar_settings)
        val progressBarAvatar : ProgressBar = rootView.findViewById(R.id.progressBarAvatar)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser!!

        CometChat.login(user.uid, authKey, object: CometChat.CallbackListener<com.cometchat.pro.models.User>(){
            override fun onSuccess(p0: com.cometchat.pro.models.User?) {

            }

            override fun onError(p0: CometChatException?) {

            }

        } )

        progressBarAvatar.visibility= View.VISIBLE
        ref = FirebaseDatabase.getInstance().getReference("Users/").child(user.uid.toLowerCase())
        val menuListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val photoUri : Uri = Uri.parse(dataSnapshot.child("photoUri").getValue(String::class.java))

                //nUser= dataSnapshot.value as User
                picasso.load(photoUri).error(R.drawable.cat_ava).into(avatarView)
                progressBarAvatar.visibility= View.GONE
            }

            override fun onCancelled(p0: DatabaseError) {
                print("Error loading user")
                progressBarAvatar.visibility= View.GONE
            }
        }
        ref.addValueEventListener(menuListener)


        listSettings = rootView.findViewById(R.id.list_settings)

        listSettings.adapter = context?.let { MyAdapter(it, optionsTitle) }

        listSettings.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position){
                    0 -> {
                        val intent = Intent(activity, EditProfileActivity::class.java)
                        startActivityForResult(intent, 10001)
                    }
                    1 -> Toast.makeText(context,"Push clicked",Toast.LENGTH_LONG).show()
                    2 -> Toast.makeText(context,"Coming Soon",Toast.LENGTH_SHORT).show()
                    3 -> {
                        mAuth.signOut()

                            CometChat.logout(object: CometChat.CallbackListener<String>(){
                                override fun onSuccess(p0: String?) {
                                    Log.d(TAG, "Logout completed successfully")
                                }

                                override fun onError(p0: CometChatException?) {
                                    Log.d(TAG, "Logout failed with exception: " + p0!!.message);
                                }

                            })


                        val intent= Intent(activity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        return rootView

    }



}