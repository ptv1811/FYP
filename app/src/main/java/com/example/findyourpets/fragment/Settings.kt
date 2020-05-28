package com.example.findyourpets.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.findyourpets.R
import com.example.findyourpets.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ornach.nobobutton.NoboButton

class Settings : Fragment() {
    private lateinit var user:FirebaseUser
    private lateinit var mAuth:FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View= inflater.inflate(R.layout.fragment_settings, container, false)

        mAuth= FirebaseAuth.getInstance()
        user= mAuth.currentUser!!

        val signOut:NoboButton= rootView.findViewById(R.id.sign_out_button)
        signOut.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                mAuth.signOut()
                val intent= Intent(activity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        return rootView

    }

    companion object {
        fun newInstance():Settings{
            return Settings()
        }
    }
}