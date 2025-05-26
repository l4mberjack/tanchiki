package com.example.tanchiki

object GameCore {
    @Volatile
    private var isPlay = false

    fun StartOrPauseTheGame(){
        isPlay = !isPlay
    }

    fun isPlaying() = isPlay

    fun pauseTheGame(){
        isPlay = false
    }
}