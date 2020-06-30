package com.example.findyourpets.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.findyourpets.R
import com.example.findyourpets.activity.UploadPostActivity

class DetailedInfo : Fragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_detailed_info, container, false)
        val previousButton: Button = rootView.findViewById(R.id.previousButton)
        val postButton: Button = rootView.findViewById(R.id.postButton)

        previousButton.setOnClickListener {
            (activity as UploadPostActivity?)!!.setCurrentItem(0, true)
        }


        return rootView
    }
}