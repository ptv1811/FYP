package com.example.findyourpets.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.findyourpets.R


class NewsFeed : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_news_feed, container, false)
    }

    companion object {
        fun newInstance():NewsFeed{
            return NewsFeed()
        }
    }
}
