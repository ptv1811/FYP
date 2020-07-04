package com.example.findyourpets.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.example.findyourpets.R
import com.example.findyourpets.`object`.Chat
import com.example.findyourpets.`object`.Message
import com.example.findyourpets.`object`.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter


class ChatActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var curUser: FirebaseUser
    private lateinit var userRef: DatabaseReference
    private lateinit var chatRef: DatabaseReference
    private lateinit var messageRef: DatabaseReference

    private lateinit var chatReceiverID: String
    private var picasso = Picasso.get()
    private var thisReceiver = User()
    private lateinit var receiveMessRef: DatabaseReference
    private var listChat = arrayListOf<Chat>()
    var TAG: String= "Cometchat"
    private val listenerID = "IncomingMessageListener1"
    private var authKey: String = "8f6c81a29d471c4ee96f5c94da978cf999f30dfa"

    private lateinit var adapter : MessagesListAdapter<Message>


    private lateinit var chatListMessage: MessagesList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custom_toolbar)

        val customView = supportActionBar!!.customView

        val chatBackButton: Button = customView.findViewById(R.id.chatBackButton)
        val chatUserName: TextView = customView.findViewById(R.id.chatUserName)
        val chatUserAvatar: CircularImageView = customView.findViewById(R.id.chatUserAvatar)
        val chatInputMessage: MessageInput = findViewById(R.id.chatMessageInput)
        chatListMessage = findViewById(R.id.chatMessageList)
        
       // var receiverUser = User()

        chatReceiverID= intent.extras!!.getString("this UserID")!!

        mAuth = FirebaseAuth.getInstance()
        curUser = mAuth.currentUser!!
        userRef = FirebaseDatabase.getInstance().reference.child("Users/").child(chatReceiverID)
        chatRef = FirebaseDatabase.getInstance().reference
        messageRef = FirebaseDatabase.getInstance().reference
        receiveMessRef = FirebaseDatabase.getInstance().reference.child("Messages/")

        //if (CometChat.getLoggedInUser() == null){
            CometChat.login(curUser.uid, authKey, object: CometChat.CallbackListener<com.cometchat.pro.models.User>(){
                override fun onSuccess(p0: com.cometchat.pro.models.User?) {

                }

                override fun onError(p0: CometChatException?) {

                }

            } )
       // }

        chatBackButton.setOnClickListener {
            finish()
        }

        val menuListener = object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("Error")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val receiver = snapshot.getValue<User>()
                //receiverUser = receiver!!
                thisReceiver= receiver!!

                chatUserName.text = receiver.name
                picasso.load(Uri.parse(receiver.photoUri)).into(chatUserAvatar)
            }

        }
        userRef.addValueEventListener(menuListener)
        Log.d("ChatReceiverID: ", chatReceiverID)

        CometChat.addLoginListener("UNIQUE", object : CometChat.LoginListener(){
            override fun loginSuccess(p0: com.cometchat.pro.models.User?) {
                Log.d("LoginListener", "loginSuccess " + p0.toString())
            }

            override fun loginFailure(p0: CometChatException?) {

            }

            override fun logoutSuccess() {

            }

            override fun logoutFailure(p0: CometChatException?) {

            }

        })



        chatInputMessage.setAttachmentsListener {
        }


        chatInputMessage.setInputListener { input ->
            //sendMessage(input.toString())
            sendMessageCometchat(input.toString())

            return@setInputListener true
        }

        val imageLoader =
            ImageLoader { imageView: ImageView?, url: String?, payload: Any? ->
                Picasso.get().load(url).into(imageView)
            }

        adapter = MessagesListAdapter(curUser.uid.toLowerCase(), imageLoader)
        chatListMessage.setAdapter(adapter)

        addIncomingMessageListener()
        getPreviousMessage()
    }

    private fun addIncomingMessageListener() {
        CometChat.addMessageListener(listenerID, object :CometChat.MessageListener(){
            override fun onTextMessageReceived(message: TextMessage?) {
                addMessage(message)
            }
        })
    }

    private fun addMessage(textMessage: TextMessage?) {
        adapter.addToStart(Message(textMessage!!), true)
    }

    private fun getPreviousMessage() {
        val messageRequest = MessagesRequest.MessagesRequestBuilder().setUID(chatReceiverID.toLowerCase()).setLimit(20)
            .build()
        messageRequest.fetchPrevious(object: CometChat.CallbackListener<List<BaseMessage>>(){
            override fun onSuccess(p0: List<BaseMessage>?) {
                addMessages(p0)

            }

            override fun onError(p0: CometChatException?) {

            }

        })
    }

    private fun addMessages(baseMessage: List<BaseMessage>?) {
        val messageWrappers : ArrayList<Message> = arrayListOf()
        for (message in baseMessage!!){
            messageWrappers.add(Message(message as TextMessage))
        }
        adapter.addToEnd(messageWrappers, true)
    }

    private fun sendMessageCometchat(message: String) {
        val textMessage = TextMessage(chatReceiverID, message, CometChatConstants.RECEIVER_TYPE_USER)

        CometChat.sendMessage(textMessage, object: CometChat.CallbackListener<TextMessage>(){
            override fun onSuccess(p0: TextMessage?) {
                addMessage(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Message sending failed with exception: " + p0?.message)
            }

        })
    }

}