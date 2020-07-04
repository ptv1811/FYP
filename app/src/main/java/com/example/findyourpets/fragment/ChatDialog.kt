package com.example.findyourpets.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.ConversationsRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.User
import com.example.findyourpets.R
import com.example.findyourpets.`object`.Author
import com.example.findyourpets.`object`.Dialog
import com.example.findyourpets.activity.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import java.util.*

class ChatDialog : Fragment(){

    private lateinit var mAuth: FirebaseAuth
    private lateinit var curUser: FirebaseUser
    private var authKey: String = "8f6c81a29d471c4ee96f5c94da978cf999f30dfa"
    private var sender: Author? = null

    private lateinit var dialogListsAdapter: DialogsListAdapter<Dialog>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView : View = inflater.inflate(R.layout.fragment_chat_dialog, container , false)

        mAuth = FirebaseAuth.getInstance()
        curUser = mAuth.currentUser!!

        val chatDialogView: DialogsList = rootView.findViewById(R.id.chatDialog)

        val imageLoader =
            ImageLoader { imageView: ImageView?, url: String?, payload: Any? ->
                Picasso.get().load(url).into(imageView)
            }

        dialogListsAdapter = DialogsListAdapter(imageLoader)

        dialogListsAdapter.setOnDialogClickListener { dialog ->
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("this UserID", dialog!!.users[1].id)
            startActivity(intent)
        }

        chatDialogView.setAdapter(dialogListsAdapter)

        CometChat.login(curUser.uid.toLowerCase(Locale.ROOT), authKey, object: CometChat.CallbackListener<User>(){
            override fun onSuccess(p0: User?) {
                sender = Author(p0!!)
                val conversationsRequest = ConversationsRequest.ConversationsRequestBuilder().setConversationType(
                    CometChatConstants.CONVERSATION_TYPE_USER
                ).build()
                conversationsRequest.fetchNext(object : CometChat.CallbackListener<List<Conversation>>(){
                    override fun onSuccess(p0: List<Conversation>?) {

                        addConversationToDialogList(p0, sender!!, imageLoader)
                    }

                    override fun onError(p0: CometChatException?) {
                    }

                })


            }

            override fun onError(p0: CometChatException?) {
            }
        } )

        return rootView
    }

    private fun addConversationToDialogList(conversations: List<Conversation>?, sender: Author, imageLoader: ImageLoader
    ) {
        val chatDialog : ArrayList<Dialog> = arrayListOf()
        for (con in conversations!!){
            chatDialog.add(Dialog(con, sender))
        }
        dialogListsAdapter.setItems(chatDialog)
    }
}