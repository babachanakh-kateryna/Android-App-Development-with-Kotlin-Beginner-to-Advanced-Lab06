package com.example.project5

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        val textOfGoogleButton = loginBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        textOfGoogleButton.text = "Continue with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 18F

        //register
        registerActivityForGoogleSignIn()

        loginBinding.buttonSignIn.setOnClickListener {
            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()
            signInUser(userEmail, userPassword)
        }
        loginBinding.buttonGoogleSignIn.setOnClickListener {
            signInGoogle()
        }
        loginBinding.textViewSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        loginBinding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    fun signInUser(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    task.exception?.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("929871202541-kvm1q19rk27kljt93ei14kddh3ba5iia.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn()
    }

    private fun signIn() {
        val signIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signIntent)
    }

    private fun registerActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    if (resultCode == RESULT_OK && data != null) {
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(data)
                        firebaseSignInWithGoogle(task)
                    }
                })
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }  catch (e : ApiException){
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount){
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener {task ->
            if (task.isSuccessful){
//                val user = auth.currentUser
            } else{

            }
        }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if (user != null){
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}