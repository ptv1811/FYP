package com.example.findyourpets.`object`

class Chat(
    var message: String = "",
    var mediaUrl: String ="",
    var timeSent: Long = 0,
    var thumbNailUrl: String ="",
    var seen: Boolean= false,
    var from: String = ""
)