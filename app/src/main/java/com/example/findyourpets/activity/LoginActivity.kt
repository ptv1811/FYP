package com.example.findyourpets.activity

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
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.findyourpets.R
import com.example.findyourpets.`object`.User
import com.facebook.*
import com.ornach.nobobutton.NoboButton
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
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {
    private val text:String = "By signing in, you agree to our Terms of Service and Privacy Policy"

    private val ss:SpannableString = SpannableString(text)
    private val fcsOrange:ForegroundColorSpan= ForegroundColorSpan(Color.parseColor("#FAC447"))
    private val fcsOrange2:ForegroundColorSpan= ForegroundColorSpan(Color.parseColor("#FAC447"))
    private val RC_SIGN_IN=1
    lateinit var mCallbackManager:CallbackManager
    private lateinit var mAuth:FirebaseAuth
    lateinit var mGoogleSignInClient:GoogleSignInClient

    private var nuser: User = User()

    private lateinit var ref:DatabaseReference
    var appID : String = "208055a14a02152"
    var region: String = "us"
    var authKey: String = "8f6c81a29d471c4ee96f5c94da978cf999f30dfa"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region)
            .build()

        CometChat.init(this, appID, appSettings, object : CometChat.CallbackListener<String?>() {
            override fun onSuccess(successMessage: String?) {

            }

            override fun onError(e: CometChatException) {

            }
        })

        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)
        val textView:TextView = findViewById(R.id.term_condition)
        changeColorWord(ss, fcsOrange, fcsOrange2,textView)

        //Initialize
        ref= FirebaseDatabase.getInstance().reference
        val fbLogin:NoboButton= findViewById(R.id.fb_login)
        val ggLogin:NoboButton=findViewById(R.id.gg_login)
        mAuth= FirebaseAuth.getInstance()
        mCallbackManager= CallbackManager.Factory.create()

        val gso:GoogleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this@LoginActivity, gso)
        if (mAuth.currentUser !=null){
            updateUI(mAuth.currentUser)
        }

        //Set FB Login to button
        fbLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, listOf("email"))
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
                    //ref=FirebaseDatabase.getInstance().getReference("Users/").child(user!!.uid)
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
                //ref=FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid)
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
            val intent= Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("ID", user.uid)

            ref.child("Users/").child(user.uid.toLowerCase()).addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    print("failed")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val tuser: User? =p0.getValue(User::class.java)
                    if (tuser==null){
                        nuser= User(user.email.toString(), user.displayName.toString(),false, user.metadata!!.creationTimestamp.toString(), true, "NA","NA","NA",user.photoUrl.toString())
                        ref.child("Users/").child("${user.uid.toLowerCase()}/").setValue(nuser)

                    }
                    else{
                        nuser=tuser

                    }
                }
            })

            val cometUser = com.cometchat.pro.models.User(user.uid, user.displayName)
            cometUser.avatar = user.photoUrl.toString()
            CometChat.createUser(cometUser, authKey, object:
                CometChat.CallbackListener<com.cometchat.pro.models.User>() {
                override fun onSuccess(p0: com.cometchat.pro.models.User?) {

                }

                override fun onError(p0: CometChatException?) {

                }

            })






            startActivity(intent)
            finish()
        }
        else TODO()
    }



    private fun changeColorWord(ss: SpannableString, fcsOrange: ForegroundColorSpan, fcsOrange2: ForegroundColorSpan, textView: TextView) {
        ss.setSpan(fcsOrange, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(fcsOrange2, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(ss)
    }
}
