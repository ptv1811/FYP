package com.example.findyourpets.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.example.findyourpets.R
import com.example.findyourpets.`object`.PostInfoAdapter
import com.shuhart.stepview.StepView

class UploadPostActivity : AppCompatActivity() {

    private lateinit var stepView: StepView
    private lateinit var postInfoView: ViewPager2
    private lateinit var postButton: Button
    private lateinit var closeButton: Button

    private var petChosen: String = "NA"
    private var petFoundOrLost: String = "NA"
    private var petGender: String = "NA"
    private var petBreed: String = "NA"
    private var petColor: String = "NA"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_post)

        stepView = findViewById(R.id.stepView)
        postInfoView = findViewById(R.id.postInfo)
        closeButton = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }

        setUpStepView()

        postInfoView.isUserInputEnabled =false
        postInfoView.adapter = PostInfoAdapter(this)
        postInfoView.run { adapter?.notifyDataSetChanged() }
    }

    private fun setUpStepView() {
        val stepList = arrayListOf("Overall Information", "Detailed Information")
        stepView.setSteps(stepList)
        //stepView.setStepsNumber(2)
    }

    fun setCurrentItem(item:Int, smoothScroll : Boolean){
        stepView.go(item, true)
        postInfoView.setCurrentItem(item, smoothScroll)
    }

}