package com.example.tanchiki.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.tanchiki.R

const val SCORE_REQUEST_CODE = 100

class ScoreActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_SCORE = "extra_score"

        fun createIntent(context: Context, score: Int): Intent{
            return Intent(context, ScoreActivity::class.java)
                .apply {
                    putExtra(EXTRA_SCORE, score)
                }
        }
    }

    var score = 0

    override fun OnCreate(){
        super.OnCreate(savedStateRegistry)
        setContentView(R.layout.activity_score)
        score = intent.getIntExtra(EXTRA_SCORE, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        finish()
    }

}