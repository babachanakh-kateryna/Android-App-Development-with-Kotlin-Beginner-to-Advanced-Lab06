package com.example.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.buttonSignOut.setOnClickListener {

            //Email and password
            FirebaseAuth.getInstance().signOut()

            //Google account
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build()
            val googleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)
            googleSignInClient.signOut().addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext, "Sign out is successful", Toast.LENGTH_SHORT).show()
                }
            }

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mainBinding.buttonStartQuiz.setOnClickListener {
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}