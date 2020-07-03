package com.example.findyourpets.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.models.User
import com.example.findyourpets.R
import com.example.findyourpets.`object`.FirebaseViewHolder
import com.example.findyourpets.`object`.Post
import com.example.findyourpets.activity.ChatActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class Profile : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var curUser: FirebaseUser
    private lateinit var ref: DatabaseReference

    private lateinit var options : FirebaseRecyclerOptions<Post>
    private lateinit var myAdapter : FirebaseRecyclerAdapter<Post, FirebaseViewHolder>
    private lateinit var postRef : DatabaseReference
    private lateinit var userRef: DatabaseReference



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        val profileAvatar: CircularImageView = rootView.findViewById(R.id.profileAvatar)
        val profileName: TextView = rootView.findViewById(R.id.profileUserName)
        val profileCreatedDate: TextView = rootView.findViewById(R.id.profileCreatedDate)
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBarAvatarProfile)
        val profilePost: RecyclerView = rootView.findViewById(R.id.profilePost)


        mAuth = FirebaseAuth.getInstance()
        curUser = mAuth.currentUser!!

        postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        val layoutManager = LinearLayoutManager(this.activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        userRef = FirebaseDatabase.getInstance().reference.child("Users")

        profilePost.layoutManager = layoutManager

        options = FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef.orderByChild("userID").equalTo(curUser.uid), Post::class.java)
            .build()
        myAdapter=
            object :FirebaseRecyclerAdapter<Post,FirebaseViewHolder>(options){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseViewHolder {
                    return FirebaseViewHolder(LayoutInflater.from(activity).inflate(R.layout.new_feed_post_view, parent, false))
                }

                override fun onBindViewHolder(holder: FirebaseViewHolder, position: Int, model: Post) {
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
                    var mUser: com.example.findyourpets.`object`.User

                    val menuEventLister = object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            print("Error")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue<com.example.findyourpets.`object`.User>()
                            mUser = user!!
                            holder.newFeedUsername.background = null
                            holder.newFeedUsername.text = mUser.name
                            val picasso = Picasso.get()

                            holder.newFeedAvatar.background = null
                            picasso.load(mUser.photoUri).into(holder.newFeedAvatar)
                        }
                    }
                    userRef.child(userID).addValueEventListener(menuEventLister)

                    holder.newFeedChat.visibility = View.INVISIBLE
                    holder.option.visibility = View.INVISIBLE
                }

            }

        myAdapter.startListening()
        profilePost.adapter = myAdapter


        ref = FirebaseDatabase.getInstance().reference.child("Users/").child(curUser.uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<com.example.findyourpets.`object`.User>()
                Picasso.get().load(Uri.parse(user!!.photoUri)).fit().into(profileAvatar)
                profileName.text = user.name
                profileCreatedDate.text = convertMillisToDate(user.createdDate)

                progressBar.visibility = View.GONE
            }

        })



        return rootView
    }

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