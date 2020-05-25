package com.example.findyourpets

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.ornach.nobobutton.NoboButton
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.reflect.Array
import java.util.*


class LoginActivity : AppCompatActivity() {
    val text:String = "By signing in, you agree to our Terms of Service and Privacy Policy"

    val ss:SpannableString = SpannableString(text)
    val fcsOrange:ForegroundColorSpan= ForegroundColorSpan(Color.parseColor("#FAC447"))
    val fcsOrange2:ForegroundColorSpan= ForegroundColorSpan(Color.parseColor("#FAC447"))
    private val RC_SIGN_IN=1
    lateinit var mCallbackManager:CallbackManager
    private lateinit var mAuth:FirebaseAuth
    lateinit var mGoogleSignInClient:GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)
        val textView:TextView = findViewById(R.id.term_condition)
        changeColorWord(ss, fcsOrange, fcsOrange2,textView)

        //Initialize
        val fbLogin:NoboButton= findViewById(R.id.fb_login)
        val ggLogin:NoboButton=findViewById(R.id.gg_login)
        mAuth= FirebaseAuth.getInstance()
        mCallbackManager= CallbackManager.Factory.create()

        val gso:GoogleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this@LoginActivity, gso)

        //Set FB Login to button
        fbLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("email"))
                LoginManager.getInstance().registerCallback(mCallbackManager, object: FacebookCallback<LoginResult>{
                    override fun onSuccess(result: LoginResult?) {
                        handleFacebookAccessToken(result!!.accessToken)
                    }

                    override fun onCancel() {
                        Log.d("cancel", "Facebook onCancel")
                    }

                    override fun onError(error: FacebookException?) {
                        Log.d("error", "Facebook onError")
                    }
                })

            }
        })

        //Set GG Login to button
        ggLogin.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                GoogleSignin()
            }
        })
    }

    private fun GoogleSignin() {
        val signInIntent=mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        //Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGN_IN){
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else
            mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account:GoogleSignInAccount=task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }
        catch (e:ApiException){

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential=GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val user=mAuth.currentUser
                updateUI(user)
            }
            else{
                Log.d("cancel", "Google failed!")
                updateUI(null)
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){
            TODO()
        }
        else TODO()
    }



    private fun changeColorWord(ss: SpannableString, fcsOrange: ForegroundColorSpan, fcsOrange2: ForegroundColorSpan, textView: TextView) {
        ss.setSpan(fcsOrange, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(fcsOrange2, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(ss)
    }
}
