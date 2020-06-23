package com.example.findyourpets.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.findyourpets.R
import com.example.findyourpets.`object`.MyAdapter
import com.example.findyourpets.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Settings : Fragment() {
    private lateinit var user:FirebaseUser
    private lateinit var mAuth:FirebaseAuth
    private lateinit var  listSettings: ListView
    private val optionsTitle: Array<String> = arrayOf("Edit Profile","Push Notification","Help Center", "Sign Out")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_settings, container, false)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser!!
        listSettings = rootView.findViewById(R.id.list_settings)


        listSettings.adapter = context?.let { MyAdapter(it, optionsTitle) }

        listSettings.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position){
                    0 -> {

                    }
                    1 -> Toast.makeText(context,"Push clicked",Toast.LENGTH_LONG).show()
                    2 -> Toast.makeText(context,"Coming Soon",Toast.LENGTH_SHORT).show()
                    3 -> {
                        mAuth.signOut()
                        val intent= Intent(activity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }


        /* val signOut:ImageView= rootView.findViewById(R.id.sign_out_button)
        signOut.setOnClickListener {
            mAuth.signOut()
            val intent= Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }*/

        return rootView

    }
}