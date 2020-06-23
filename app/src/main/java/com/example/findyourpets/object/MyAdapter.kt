package com.example.findyourpets.`object`

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.findyourpets.R

class MyAdapter(private val mContext:Context, private var optionsTitle: Array<String>) : BaseAdapter(){
    private val inflater:LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return optionsTitle.size
    }

    override fun getItem(position: Int): Any {
        return optionsTitle[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val row:View // = inflater.inflate(R.layout.options_view,parent,false)
        val title: TextView

        if (position == 1)
        {
            row = inflater.inflate(R.layout.notification_push_layout, parent,false)
            title = row.findViewById(R.id.options_text)
            row.isEnabled=false
            row.setOnClickListener(null)
        }
        else{
            row = inflater.inflate(R.layout.options_view,parent,false)
            title = row.findViewById(R.id.options_title)
        }

        val populatedTitle: String = getItem(position) as String
        title.text=populatedTitle

        if (title.text== "Sign Out")
            title.setTextColor(ContextCompat.getColor(mContext, R.color.myOrange))
        return row
    }
}