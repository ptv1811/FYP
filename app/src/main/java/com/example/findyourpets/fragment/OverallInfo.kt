package com.example.findyourpets.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.findyourpets.R
import com.example.findyourpets.activity.UploadPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.thebluealliance.spectrum.SpectrumPalette

class OverallInfo: Fragment(){

    private lateinit var user: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val dogBreedOptions: Array<String> = arrayOf("Corgi", "Pug", "Poodle", "Vietnamese", "Golden Retriever", "Others")
    private val catBreedOptions: Array<String> = arrayOf("British Longhair", "British Shorthair","Sphynx", "Vietnamese", "Russian", "Others")
    var petChosen: String = "NA"
    var color: Int = 0
    private lateinit var nextButton: Button



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView: View = inflater.inflate(R.layout.fragment_overall_info, container,false)

        val dogButton: Button = rootView.findViewById(R.id.dogButton)
        val catButton: Button = rootView.findViewById(R.id.catButton)
        val lostButton: Button = rootView.findViewById(R.id.lostButton)
        val foundButton: Button = rootView.findViewById(R.id.foundButton)
        val maleButton: Button = rootView.findViewById(R.id.maleButton)
        val femaleButton: Button = rootView.findViewById(R.id.femaleButton)
        val breedOption: Spinner = rootView.findViewById(R.id.breedOption)
        val colorPicker: SpectrumPalette = rootView.findViewById(R.id.colorPicker)
        nextButton= rootView.findViewById(R.id.nextButton)

        var petFoundOrLost = "NA"
        var petGender = "NA"
        var petBreed = "NA"
        var petBreedAdapter: ArrayAdapter<String>?
        var petColor = "NA"

        dogButton.setOnClickListener {
            dogButton.isSelected = true
            catButton.isSelected = false
            petChosen = "Dog"
            Log.d("Pet chosen: ", petChosen)
            petBreedAdapter= activity?.applicationContext?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, dogBreedOptions) }!!
            breedOption.adapter = petBreedAdapter
            petBreed = breedOption.getItemAtPosition(breedOption.selectedItemPosition).toString()

        }
        catButton.setOnClickListener {
            catButton.isSelected = true
            dogButton.isSelected = false
            petChosen = "Cat"
            petBreedAdapter= activity?.applicationContext?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, catBreedOptions) }!!
            breedOption.adapter = petBreedAdapter
            petBreed = breedOption.getItemAtPosition(breedOption.selectedItemPosition).toString()
        }

        lostButton.setOnClickListener {
            foundButton.isSelected = false
            lostButton.isSelected = true
            petFoundOrLost = "Lost"
        }



        foundButton.setOnClickListener {
            foundButton.isSelected = true
            lostButton.isSelected = false
            petFoundOrLost = "Found"
        }

        maleButton.setOnClickListener {
            maleButton.isSelected = true
            femaleButton.isSelected = false
            petGender = "Male"
        }

        femaleButton.setOnClickListener {
            femaleButton.isSelected = true
            maleButton.isSelected = false
            petGender = "Female"
        }

        colorPicker.setOnColorSelectedListener{
            clr -> color = clr
            val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color)
            Log.d("Color", hexColor)
            petColor = hexColor
        }

        nextButton.setOnClickListener {
            (activity as UploadPostActivity?)!!.setCurrentItem(1, true)
            val sharePreference = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharePreference!!.edit()
            editor.putString("Pet Chosen", petChosen)
            editor.putString("Pet Lost or Found", petFoundOrLost)
            editor.putString("Pet Gender", petGender)
            editor.putString("Pet Breed", petBreed)
            editor.putString("Pet Color", petColor)
            editor.apply()
        }




        Log.d("Pet outside onclick: ", petChosen)
        return rootView
    }


}