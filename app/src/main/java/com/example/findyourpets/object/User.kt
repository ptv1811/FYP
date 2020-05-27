package com.example.findyourpets.`object`

class User(
    email: String="",
    name: String="",
   // id: Int,
    isAdmin: Boolean=false,
    createdDate: String="",
    var isActive: Boolean=false,
    var phoneNumber: String="",
    var gender: String="",
    var favoritePet: String=""
) : Account(email, name, isAdmin, createdDate){
    constructor() : this("","",false,"")
}
