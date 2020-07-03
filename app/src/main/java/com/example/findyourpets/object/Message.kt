package com.example.findyourpets.`object`

import com.cometchat.pro.models.TextMessage
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*

class Message(private val message:TextMessage) : IMessage {


    /*...*/

    override fun getId(): String {
        return message.muid
    }

    override fun getText(): String {
        return message.text
    }



    override fun getCreatedAt(): Date {
        return Date(message.sentAt * 1000)
    }

    override fun getUser(): IUser {
        return Author(message.sender)
    }
}