package com.example.tanchiki

import android.app.Activity
import android.view.View
import android.view.animation.AnimationUtils
import com.example.tanchiki.activities.SCORE_REQUEST_CODE
import com.example.tanchiki.activities.ScoreActivity
import com.example.tanchiki.activities.binding

class GameCore(private val activity:Activity) {
    @Volatile
    private var isPlay = false
    private var isPlayerOrBaseDestroyed = false
    private var isPlayerWin = false

    fun startOrPauseTheGame(){
        isPlay = !isPlay
    }

    fun isPlaying() = isPlay && !isPlayerOrBaseDestroyed && !isPlayerWin

    fun playerWon(score: Int){
        isPlayerWin = true
        activity.startActivityForResult(
            ScoreActivity.createIntent(activity, score),
            SCORE_REQUEST_CODE
        )
    }
    fun pauseTheGame(){
        isPlay = false
    }

    fun destroyPlayerOrBase(){
        isPlayerOrBaseDestroyed = true
        pauseTheGame()
        animateEndGame()
    }

    private fun animateEndGame() {
        activity.runOnUiThread{
            binding.gameOverText.visibility = View.VISIBLE
            val slideUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up)
            binding.gameOverText.startAnimation(slideUp)
        }
    }
}