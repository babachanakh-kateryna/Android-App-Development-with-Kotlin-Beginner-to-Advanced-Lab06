package com.example.project5

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumber = 0

    var userCorrect = 0
    var userWrong = 0


    lateinit var timer : CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    val questions = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)

        do {
            val number = Random.nextInt(1, 11)
            Log.d("number", number.toString())
            questions.add(number)
        }while (questions.size < 5)

        Log.d("numberOfQuestions", questions.toString())

        gameLogic()

        quizBinding.buttonNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }
        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
        }

        quizBinding.textViewA.setOnClickListener {
            chooseLogic("a")
        }
        quizBinding.textViewB.setOnClickListener {
            chooseLogic("b")
        }
        quizBinding.textViewC.setOnClickListener {
            chooseLogic("c")
        }
        quizBinding.textViewD.setOnClickListener {
            chooseLogic("d")
        }
    }

    private fun gameLogic(){

        restoreOptions()

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                questionCount = snapshot.childrenCount.toInt()

                if (questionNumber < questions.size){
                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE
                    quizBinding.linearLayoutInformation.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestions.visibility = View.VISIBLE

                    startTimer()

                } else {
                    showDialogMessage()
                }

                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun chooseLogic(userAnswer: String){
        pauseTimer()

        val textView = when(userAnswer){
            "a" -> quizBinding.textViewA
            "b" -> quizBinding.textViewB
            "c" -> quizBinding.textViewC
            else -> quizBinding.textViewD
        }

        if (correctAnswer == userAnswer){
            textView.setBackgroundColor(Color.GREEN)
            actionCorrectChosen()
        }else{
            textView.setBackgroundColor(Color.RED)
            actionIncorrectChosen()
        }

        disableClickableOfOptions()
    }

    private fun actionCorrectChosen(){
        userCorrect++
        quizBinding.textViewCorrect.text = userCorrect.toString()
    }

    private fun actionIncorrectChosen(){
        userWrong++
        quizBinding.textViewWrong.text = userWrong.toString()
        findAnswer()
    }

    private fun findAnswer(){
        when(correctAnswer){
            "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            else -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    private fun disableClickableOfOptions(){
        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }

    private fun restoreOptions(){
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true
    }

    fun startTimer(){
        timer = object : CountDownTimer(leftTime, 1000){
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                disableClickableOfOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Time is up? continue with next question"
                timerContinue = false
            }
        }.start()

        timerContinue = true
    }

    fun updateCountDownText(){
        val remainingTime : Int = (leftTime/1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }
    fun pauseTimer(){
        timer.cancel()
        timerContinue = false
    }
    fun resetTimer(){
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    fun sendScore(){
        user?.let {
            val userUID = it.uid
            scoreRef.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {
                Toast.makeText(applicationContext, "Scores saved in database", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun showDialogMessage(){
        val dialogMessage = AlertDialog.Builder(this@QuizActivity)
        dialogMessage.setTitle("Quiz Game")
        dialogMessage.setMessage("You have answered all questions\n" +
                "Do you want to see results?")
        dialogMessage.setCancelable(false)
        dialogMessage.setPositiveButton("See result"){dialogWindow, position ->
            sendScore()
        }
        dialogMessage.setNegativeButton("Play again"){dialogWindow, position ->
            val intent = Intent(this@QuizActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialogMessage.create().show()
    }

}