package com.example.findyourpets.activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.example.findyourpets.R
import com.example.findyourpets.`object`.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {

    private lateinit var currentUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private var storage = Firebase.storage
    private lateinit var database: DatabaseReference

    private var IMAGE_REQUEST: Int = 1000
    private lateinit var avatarUri : Uri
    private lateinit var  storageRef : StorageReference
    private lateinit var imageRef: StorageReference
    lateinit var avatarEdit : CircularImageView
    lateinit var saveButton : Button

    private val genderOption: Array<String> = arrayOf("Male", "Female")
    private val favoritePetOption: Array<String> = arrayOf("Dog", "Cat", "Others")
    private lateinit var genderAdapter: ArrayAdapter<String>
    private lateinit var favoritePetAdapter: ArrayAdapter<String>
    private lateinit var muser: User

    private val picasso = Picasso.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*Declare and init variables*/

        mAuth=FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        setContentView(R.layout.activity_edit_profile)

        storageRef= storage.reference
        imageRef = storageRef.child("${currentUser.uid.toLowerCase()}/avatar.jpg")
        database = Firebase.database.getReference("Users/").child(currentUser.uid.toLowerCase())

        val uploadAvatarButton: Button = findViewById(R.id.upload_avatar)
        val backButton: Button = findViewById(R.id.back_button)
        avatarEdit = findViewById(R.id.avatar_edit)
        saveButton = findViewById(R.id.save_button)
        genderAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, genderOption)
        favoritePetAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, favoritePetOption)

        val fullNameEdit : EditText = findViewById(R.id.fullNameEdit)
        val emailEdit : EditText = findViewById(R.id.emailEdit)
        val phoneEdit : EditText = findViewById(R.id.phoneEdit)
        val genderEdit : Spinner = findViewById(R.id.genderEdit)
        val favoritePetEdit : Spinner = findViewById(R.id.favoritePetEdit)
        genderEdit.adapter = genderAdapter
        favoritePetEdit.adapter = favoritePetAdapter

        /*End of declare and init variables*/

        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>()
                muser= user!!
                fullNameEdit.setText(user.name)
                phoneEdit.setText(user.phoneNumber)

                if (user.email != "null") {
                    emailEdit.setText(user.email)
                }
                avatarUri = Uri.parse(user.photoUri)
                picasso.load(user.photoUri).error(R.drawable.cat_ava).into(avatarEdit)

                if (user.gender != "NA"){
                    val genderSelected: Int = genderAdapter.getPosition(muser.gender)
                    genderEdit.setSelection(genderSelected)
                }

                if (user.favoritePet != "NA"){
                    val favoritePetSelected: Int = favoritePetAdapter.getPosition(muser.favoritePet)
                    favoritePetEdit.setSelection(favoritePetSelected)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                print("Error loading user")
            }
        }

        database.addValueEventListener(menuListener)

        backButton.setOnClickListener{
            finish()
        }

        uploadAvatarButton.setOnClickListener{
            openExternalStorage()
        }

        saveButton.setOnClickListener{
            uploadImageToFireBaseStorage(avatarUri)
            muser.name = fullNameEdit.text.toString()
            if (emailEdit.text.toString() == ""){
                muser.email = "null"
            }
            else
                muser.email = emailEdit.text.toString()
            Log.d("email: ", emailEdit.text.toString())
            muser.phoneNumber = phoneEdit.text.toString()
            muser.gender = genderEdit.getItemAtPosition(genderEdit.selectedItemPosition).toString()
            Log.d("gender: ", muser.gender)
            muser.favoritePet = favoritePetEdit.getItemAtPosition(favoritePetEdit.selectedItemPosition).toString()

            database.setValue(muser)
            finish()
        }
    }

    private fun openExternalStorage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            avatarUri = data.data!!
            avatarEdit.setImageURI(avatarUri)

        }
    }

    private fun uploadImageToFireBaseStorage(avatarUri: Uri) {
        val progressDialog = ProgressBar(this, null, R.attr.progressBarStyle)
        progressDialog.visibility = View.VISIBLE

        imageRef.putFile(avatarUri).addOnSuccessListener {
            progressDialog.visibility = View.GONE
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val newAvatar: String = uri.toString()
                database.child("photoUri").setValue(newAvatar)
            }
        }
            .addOnFailureListener{
                //Toast.makeText(this, "Failed to upload to storage", Toast.LENGTH_SHORT).show()
                progressDialog.visibility= View.GONE
            }
    }

}