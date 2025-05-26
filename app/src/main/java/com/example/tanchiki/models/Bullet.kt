package com.example.tanchiki.models

import android.view.View

data class Bullet(
    val view: View,
    val direction: com.example.tanchiki.enums.Direction,
    val tank: Tank,
    var canMoveFurther: Boolean = true
) {

    private val allBullet = mutableListOf<Bullet>()
    init {
        moveAllBullets()
    }

    private fun addNewBulletForTank(tank: Tank){
        val view = container.findViewById(tank.element.viewId) ?: return
        if(tank.alreadyHasBullet()) return
        allBullet.add(Bullet(createBullet(view, tank.direction), tank.direction, tank))
    }

    private fun createBullet(): Boolean = allBullet.firstOrNull{it.tank == this} != null

    fun moveAllBullets() {
        Thread(Runnable {
            while(true){
                Thread.sleep(30)
            }
        })
    }
}