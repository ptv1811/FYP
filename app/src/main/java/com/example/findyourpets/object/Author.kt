package com.example.findyourpets.`object`

import com.cometchat.pro.models.User
import com.stfalcon.chatkit.commons.models.IUser

class Author(private val user: User) : IUser {
    /*...*/

    override fun getId(): String {
        return user.uid
    }

    override fun getName(): String {
        return user.name
    }

    override fun getAvatar(): String {
        return user.avatar
    }
}