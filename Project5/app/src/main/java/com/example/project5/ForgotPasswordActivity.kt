package com.example.project5

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var forgotBinding: ActivityForgotPasswordBinding

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgotBinding.root)

        forgotBinding.buttonResetPassword.setOnClickListener {
            val userEmail = forgotBinding.editTextForgotPasswordEmail.text.toString()

            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    finish()
                    Toast.makeText(applicationContext, "We sent a reset email to your email address", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}