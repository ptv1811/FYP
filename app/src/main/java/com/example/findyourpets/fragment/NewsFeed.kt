package com.example.findyourpets.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findyourpets.R
import com.example.findyourpets.`object`.FirebaseViewHolder
import com.example.findyourpets.`object`.Post
import com.example.findyourpets.`object`.User
import com.example.findyourpets.activity.ChatActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.new_feed_post_view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("NAME_SHADOWING")
class NewsFeed : Fragment() {

    private lateinit var newFeeds: RecyclerView
    private lateinit var listPost : ArrayList<Post>
    private lateinit var options : FirebaseRecyclerOptions<Post>
    private lateinit var myAdapter : FirebaseRecyclerAdapter<Post, FirebaseViewHolder>
    private lateinit var postRef : DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var curUser: FirebaseUser
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    var postID: String = "NA"

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        myAdapter.startListening()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView= inflater.inflate(R.layout.fragment_news_feed, container, false)

        mAuth= FirebaseAuth.getInstance()
        curUser = mAuth.currentUser!!

        listPost = ArrayList()
        newFeeds = rootView.findViewById(R.id.newFeed)

        val layoutManager = LinearLayoutManager(this.activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        newFeeds.layoutManager = layoutManager
        newFeeds.visibility = View.INVISIBLE




        postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        shimmerFrameLayout= rootView.findViewById(R.id.shimmerLayout)
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()

        options = FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef,Post::class.java).build()

        myAdapter =
            object : FirebaseRecyclerAdapter<Post, FirebaseViewHolder>(options) {
                override fun onBindViewHolder(holder: FirebaseViewHolder, position: Int, model: Post) {

                    disableShimmer()


                    holder.newFeedName.background = null
                    holder.newFeedName.text = "Name: " + model.petName

                    holder.newFeedLostAt.background = null
                    holder.newFeedLostAt.text = "Lost/Found at: "+ model.petLostOrFoundAt

                    holder.newFeedWeight.background = null
                    holder.newFeedWeight.text = "Weight: " + model.petWeight

                    holder.newFeedPhone.background = null
                    holder.newFeedPhone.text = "Phone: " + model.phoneNumber

                    holder.newFeedNote.background = null
                    holder.newFeedNote.text = "Note: " + model.note

                    holder.newFeedHashtag.background = null
                    holder.newFeedHashtag.text = "#${model.petChosen}, #${model.petFoundOrLost}, #${model.petBreed}, #${model.petGender}, #${colorToStringUtil(model.petColor)}"

                    holder.newFeedDate.background = null
                    holder.newFeedDate.text = convertMillisToDate(model.date)

                    val picasso = Picasso.get()

                    holder.newFeedPetImage.background = null
                    picasso.load(Uri.parse(model.petImage)).into(holder.newFeedPetImage)


                    val userID : String = model.userID
                    var mUser: User

                    val menuEventLister = object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            print("Error")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue<User>()
                            mUser = user!!
                            holder.newFeedUsername.background = null
                            holder.newFeedUsername.text = mUser.name
                            val picasso = Picasso.get()

                            holder.newFeedAvatar.background = null
                            picasso.load(mUser.photoUri).into(holder.newFeedAvatar)
                        }
                    }
                    userRef.child(userID).addValueEventListener(menuEventLister)

                    holder.newFeedChat.setOnClickListener {

                        if (userID == curUser.uid){
                            Toast.makeText(context, "You can't chat to yourself", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val chatIntent = Intent(context, ChatActivity::class.java)
                            chatIntent.putExtra("this UserID", userID)
                            startActivity(chatIntent)
                        }
                    }

                    holder.option.setOnClickListener {
                        Log.d("position: ", holder.adapterPosition.toString())
                        Log.d("position1: ", getRef(holder.adapterPosition).toString())
                        val reportOption = ReportDialogFragment()
                        val sharedPreferences = activity?.getSharedPreferences("reportPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences!!.edit()


                        editor.putString("userGetReported", userID)
                        postID = getRef(holder.adapterPosition).toString().substringAfter("Posts/")
                        editor.putString("postGetReportedID", postID)
                        editor.apply()

                        reportOption.show(childFragmentManager, "ReportBottomSheet")
                    }

                }

                override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FirebaseViewHolder {
                    return FirebaseViewHolder(LayoutInflater.from(activity).inflate(R.layout.new_feed_post_view, viewGroup, false))

                }
            }
        myAdapter.startListening()
        newFeeds.adapter = myAdapter

        return rootView
    }

    private fun disableShimmer() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.setShimmer(null)
        shimmerFrameLayout.visibility = View.GONE
        newFeeds.visibility = View.VISIBLE
    }

    private fun convertMillisToDate(date: String) : String {
        val formatter = SimpleDateFormat("dd/MM/yy")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date.toLong()
        return formatter.format(calendar.time)
    }

    private fun colorToStringUtil(colorCode: String): String{
        var color= "NA"
        when(colorCode) {
            "#EB7C27" -> {
                color= "Dark Orange"
            }
            "#817B6E" -> {
                color= "Grey"
            }
            "#000000" -> {
                color= "Black"
            }
            "#977322" -> {
                color= "Brown"
            }
            "#FFFFFF" -> {
                color= "White"
            }
        }
        return color
    }

}
