package com.example.findyourpets

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.ornach.nobobutton.NoboButton

class LoginActivity : AppCompatActivity() {
    val text:String = "By signing in, you agree to our Terms of Service and Privacy Policy"

    val ss:SpannableString = SpannableString(text)
    val fcsOrange:ForegroundColorSpan= ForegroundColorSpan(Color.parseColor("#FAC447"))
    val fcsOrange2:ForegroundColorSpan= ForegroundColorSpan(Color.parseColor("#FAC447"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val textView:TextView = findViewById(R.id.term_condition)
        changeColorWord(ss, fcsOrange, fcsOrange2,textView)

        val fbLogin:NoboButton= findViewById(R.id.fb_login)
        val ggLogin:NoboButton=findViewById(R.id.gg_login)





    }

    private fun changeColorWord(ss: SpannableString, fcsOrange: ForegroundColorSpan, fcsOrange2: ForegroundColorSpan, textView: TextView) {
        ss.setSpan(fcsOrange, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(fcsOrange2, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(ss)
    }
}
