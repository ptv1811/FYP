package com.example.findyourpets.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.findyourpets.R
import com.example.findyourpets.`object`.Post
import com.example.findyourpets.activity.UploadPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_detailed_info.*

class DetailedInfo : Fragment(){
    private var IMAGE_REQUEST: Int = 1000
    private var petImageUri : Uri? = null
    private var storage = Firebase.storage
    private lateinit var petImage: ImageView
    private lateinit var currentUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var imageRef: StorageReference
    private lateinit var  storageRef : StorageReference
    private var petImageUrl: String = "NA"
    private lateinit var database: DatabaseReference
    private var postID : String = "NA"



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_detailed_info, container, false)

        val prefs : SharedPreferences = activity?.getSharedPreferences("MyPrefs", MODE_PRIVATE)!!

        val previousButton: Button = rootView.findViewById(R.id.previousButton)
        val postButton: Button = rootView.findViewById(R.id.postButton)
        val uploadPetImageButton: Button = rootView.findViewById(R.id.uploadPetImageButton)

        val petWeightEdit: EditText = rootView.findViewById(R.id.petWeightEdit)
        val petName: EditText = rootView.findViewById(R.id.petNameEdit)
        val petLostAt: EditText = rootView.findViewById(R.id.petLostAtEdit)
        val petPhone: EditText = rootView.findViewById(R.id.petPhoneEdit)
        val petNote: EditText = rootView.findViewById(R.id.petNote)
        val petChosen: String = prefs.getString("Pet Chosen",null)!!
        val petLostOrFound: String = prefs.getString("Pet Lost or Found", null)!!
        val petGender: String = prefs.getString("Pet Gender", null)!!
        val petBreed: String = prefs.getString("Pet Breed", null)!!
        val petColor: String = prefs.getString("Pet Color", null)!!
        val datePost: String = System.currentTimeMillis().toString()


        Log.d("petchosen", petChosen)


        petImage = rootView.findViewById(R.id.petImage)

        mAuth=FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        ref = FirebaseDatabase.getInstance().reference

        storageRef= storage.reference


        uploadPetImageButton.setOnClickListener {
            openExternalStorage()
        }

        previousButton.setOnClickListener {
            (activity as UploadPostActivity?)!!.setCurrentItem(0, true)
        }

        postButton.setOnClickListener {

            if (TextUtils.isEmpty(petName.text.toString())){
                petName.error = "This field cannot be empty"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(petLostAt.text.toString())){
                petLostAt.error = "This field cannot be empty"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(petPhone.text.toString())){
                petPhone.error = "This field cannot be empty"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(petWeightEdit.text.toString())){
                petWeightEdit.error = "This field cannot be empty"
                return@setOnClickListener
            }


            val currentUserPost = Post(currentUser.uid, datePost, petChosen, petLostOrFound,
            petGender, petBreed, petColor, petImageUri.toString(), petName.text!!.toString(), petLostAt.text!!.toString(),
            petPhone.text!!.toString(), petWeightEdit.text!!.toString(), petNote.text!!.toString())

            postID = ref.child("Posts/").push().key!!
            ref.child("Posts/").child(postID).setValue(currentUserPost)

            imageRef = storageRef.child("${currentUser.uid}/${postID}/postImage.jpg")
            petImageUri?.let { it1 -> uploadImageToFireBaseStorage(it1) }
            requireActivity().finish()
            //database = FirebaseDatabase.getInstance().reference.child("Posts/").child(postID)
        }


        return rootView
    }

    private fun openExternalStorage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null){
            petImageUri = data.data!!
            petImage.setImageURI(petImageUri)

        }
    }

    private fun uploadImageToFireBaseStorage(avatarUri: Uri) {
        val progressDialog = ProgressBar(this.context, null, R.attr.progressBarStyle)
        progressDialog.visibility = View.VISIBLE

        imageRef.putFile(avatarUri).addOnSuccessListener {
            progressDialog.visibility = View.GONE
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val newPetImage: String = uri.toString()
                ref.child("Posts/").child(postID).child("petImage").setValue(newPetImage)
                petImageUrl = newPetImage
            }
        }
            .addOnFailureListener{
                //Toast.makeText(this, "Failed to upload to storage", Toast.LENGTH_SHORT).show()
                progressDialog.visibility= View.GONE
            }
    }
}