package com.example.findyourpets.`object`

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.findyourpets.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.mikhaellopez.circularimageview.CircularImageView

class FirebaseViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    //var shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout)
    var newFeedAvatar: CircularImageView = itemView.findViewById(R.id.newFeedAvatar)
    var newFeedUsername: TextView = itemView.findViewById(R.id.newFeedUsername)
    var newFeedDate: TextView = itemView.findViewById(R.id.newFeedDate)
    var newFeedName: TextView = itemView.findViewById(R.id.newFeedName)
    var newFeedLostAt: TextView = itemView.findViewById(R.id.newFeedLostAt)
    var newFeedWeight: TextView = itemView.findViewById(R.id.newFeedWeight)
    var newFeedPhone: TextView = itemView.findViewById(R.id.newFeedPhone)
    var newFeedNote: TextView = itemView.findViewById(R.id.newFeedNote)
    var newFeedHashtag: TextView = itemView.findViewById(R.id.newFeedHashtag)
    var newFeedPetImage: ImageView = itemView.findViewById(R.id.newFeedPetImage)
    var newFeedChat: Button = itemView.findViewById(R.id.newFeedChat)
    var option: Button = itemView.findViewById(R.id.newFeedOption)

}