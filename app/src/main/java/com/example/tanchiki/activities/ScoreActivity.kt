package com.example.tanchiki.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanchiki.R
import com.example.tanchiki.databinding.ActivivtyScoreBinding
import com.example.tanchiki.sounds.ScoreSoundPlayer

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
    lateinit var binding: ActivivtyScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivivtyScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        score = intent.getIntExtra(EXTRA_SCORE, 0)
        scoreSoundPlayer.playScoreSound()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private val scoreSoundPlayer by lazy{
        ScoreSoundPlayer(this, soundReadyListener = {
            startScoreCounting()
        })
    }

    private fun startScoreCounting() {
        Thread(Runnable {
            var currentScore = 0
            while(currentScore <= score){
                runOnUiThread{
                    binding.scoreTextView.text = currentScore.toString()
                    currentScore += 100
                }
                Thread.sleep(150)
            }
            scoreSoundPlayer.pauseScoreSound()
        }).start()
    }

    override fun onPause() {
        super.onPause()
        scoreSoundPlayer.pauseScoreSound()
    }
}