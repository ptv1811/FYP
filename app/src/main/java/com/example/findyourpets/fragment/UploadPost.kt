package com.example.findyourpets.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.findyourpets.R

class UploadPost : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_upload_post, container, false)
    }

    companion object {
        fun newInstance():UploadPost{
            return UploadPost()
        }
    }
}