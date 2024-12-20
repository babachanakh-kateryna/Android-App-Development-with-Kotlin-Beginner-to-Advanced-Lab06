package com.example.project5

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {

    lateinit var resultBinding: ActivityResultBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("scores")
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var userCorrect = ""
    var userWrong = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(resultBinding.root)

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {
                    var userUID = it.uid
                    userCorrect = snapshot.child(userUID).child("correct").value.toString()
                    userWrong = snapshot.child(userUID).child("wrong").value.toString()

                    resultBinding.textViewCorrect.text = "Correct answers: " + userCorrect
                    resultBinding.textViewWrong.text = "Wrong answers: " + userWrong
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        resultBinding.buttonAgain.setOnClickListener {
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        resultBinding.buttonExit.setOnClickListener {
            finish()
        }
    }
}