package com.example.findyourpets.`object`

import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.models.User
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*
import kotlin.collections.ArrayList

class Dialog(private val conversation: Conversation, private val sender: Author) : IDialog<IMessage> {
    override fun getDialogPhoto(): String {
      return Author(conversation.conversationWith as User).avatar
    }

    override fun getUnreadCount(): Int {
        return conversation.unreadMessageCount
    }


    override fun getId(): String {
        return conversation.conversationId
    }

    override fun getUsers(): ArrayList<IUser> {

        val listUser: ArrayList<IUser> = arrayListOf()
        listUser.add(sender)
        listUser.add(Author(conversation.conversationWith as User))
        return listUser
    }

    override fun getLastMessage(): IMessage? {
        val message: TextMessage = conversation.lastMessage as TextMessage

        return Message(message)
    }

    override fun getDialogName(): String {
        return Author(conversation.conversationWith as User).name
    }

    override fun setLastMessage(message: IMessage?) {
        this.lastMessage = message
    }

}