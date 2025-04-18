package com.example.tanchiki.drawers


import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.tanchiki.CELL_SIZE
import com.example.tanchiki.R
import com.example.tanchiki.enums.Direction
import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.models.Element
import utils.checkTankCanMoveThroughBorder
import utils.getElementByCoordinates
import utils.runOnUiThread

private const val BULLET_WIDTH = 15
private const val BULLET_HEIGHT = 15

class BulletDrawer (private val container: FrameLayout){
    private var canBulletGoFurther = true
    private var bulletThread: Thread? = null

    private fun checkBulletThreadLive() = bulletThread != null && bulletThread!!.isAlive

    fun makeBulletMove(
        myTank: View,
        currentDirection: Direction,
        elementsOnContainer: MutableList<Element>
    ){
        canBulletGoFurther = true
        if (!checkBulletThreadLive()) {
            bulletThread = Thread(Runnable{
                val bullet = createBullet(myTank, currentDirection)
                while (bullet.checkTankCanMoveThroughBorder(
                        Coordinate(bullet.top, bullet.left)
                    ) && canBulletGoFurther
                ) {
                    when (currentDirection){
                        Direction.UP -> (bullet.layoutParams as FrameLayout.LayoutParams).topMargin -= BULLET_HEIGHT
                        Direction.DOWN -> (bullet.layoutParams as FrameLayout.LayoutParams).topMargin += BULLET_HEIGHT
                        Direction.LEFT -> (bullet.layoutParams as FrameLayout.LayoutParams).leftMargin -= BULLET_HEIGHT
                        Direction.RIGHT -> (bullet.layoutParams as FrameLayout.LayoutParams).leftMargin += BULLET_HEIGHT
                    }
                    Thread.sleep(30)
                    chooseBehaviourInTermsOfDir(
                        elementsOnContainer,
                        currentDirection,
                        Coordinate(
                            (bullet.layoutParams as FrameLayout.LayoutParams).topMargin,
                            (bullet.layoutParams as FrameLayout.LayoutParams).leftMargin))
                    container.runOnUiThread {
                        container.removeView(bullet)
                        container.addView(bullet)
                    }
                }
                container.runOnUiThread{
                    container.removeView(bullet)
                }
            })
            bulletThread!!.start()
        }
    }
    
    private fun createBullet(myTank: View, currentDirection: Direction): ImageView{
        return ImageView(container.context)
            .apply{
                this.setImageResource(R.drawable.bullet)
                this.layoutParams = FrameLayout.LayoutParams(BULLET_WIDTH, BULLET_HEIGHT)
                val bulletCoordinate = getBulletCoordinates(this, myTank, currentDirection)
                (this.layoutParams as FrameLayout.LayoutParams).topMargin = bulletCoordinate.top
                (this.layoutParams as FrameLayout.LayoutParams).leftMargin = bulletCoordinate.left
                this.rotation = currentDirection.rotation
            }
    }

    private fun getCoordinatesForTopOrBottomDir(bulletCoordinate: Coordinate): List<Coordinate>{
        val leftCell = bulletCoordinate.left - bulletCoordinate.left % CELL_SIZE
        val rightCell = leftCell + CELL_SIZE
        val topCoordinate = bulletCoordinate.top - bulletCoordinate.top %  CELL_SIZE
        return listOf(
            Coordinate(topCoordinate,leftCell),
            Coordinate(topCoordinate,rightCell)
        )
    }

    private fun getCoordinatesForLeftOrRightDir(bulletCoordinate: Coordinate): List<Coordinate>{
        val topCell = bulletCoordinate.top - bulletCoordinate.top % CELL_SIZE
        val bottomtCell = topCell + CELL_SIZE
        val leftCoordinate = bulletCoordinate.left - bulletCoordinate.left %  CELL_SIZE
        return listOf(
            Coordinate(topCell,leftCoordinate),
            Coordinate(bottomtCell,leftCoordinate)
        )
    }

    private fun chooseBehaviourInTermsOfDir(
        elementsOnContainer: MutableList<Element>,
        currentDirection: Direction,
        bulletCoordinate: Coordinate
    ){
        when(currentDirection) {
            Direction.DOWN, Direction.UP -> {
                compareCollections(elementsOnContainer, getCoordinatesForTopOrBottomDir(bulletCoordinate))
            }
            Direction.LEFT, Direction.RIGHT -> {
                compareCollections(elementsOnContainer, getCoordinatesForLeftOrRightDir(bulletCoordinate))
            }
        }
    }

    private fun compareCollections(
        elementsOnContainer: MutableList<Element>,
        detectedCoordinatesList: List<Coordinate>
    ){
        detectedCoordinatesList.forEach{
            val element = getElementByCoordinates(it, elementsOnContainer)
            removeElementsAndStopBullet(element, elementsOnContainer)

        }
    }

    private fun removeElementsAndStopBullet(
        element:Element?,
        elementsOnContainer: MutableList<Element>
    ){
        if (element != null){
            if (element.material.tankConGoThrough){
                return
            }
            if (element.material.simpleBulletCanDestroy){
                stopBullet()
                removeView(element)
                elementsOnContainer.remove(element)
            } else {
                stopBullet()
            }
        }
    }

    private fun stopBullet() {
        canBulletGoFurther = false
    }

    private fun removeView(element: Element?) {
        val activity = container.context as Activity
        activity.runOnUiThread{

        }
    }

    fun getBulletCoordinates(
        bullet:ImageView,
        myTank: View,
        currentDirection: Direction
    ): Coordinate {
        val tankLeftTopCoordinate = Coordinate(myTank.top,myTank.left)
        return when (currentDirection){
            Direction.UP ->  Coordinate(
                    top = tankLeftTopCoordinate.top - bullet.layoutParams.height,
                    left = getDistanceToMiddleOfTank(
                        tankLeftTopCoordinate.left, bullet.layoutParams.width
                    )
                )

            Direction.DOWN -> Coordinate(
                    top = tankLeftTopCoordinate.top + myTank.layoutParams.height,
                    left = getDistanceToMiddleOfTank(
                        tankLeftTopCoordinate.left, bullet.layoutParams.width
                    )
                )

            Direction.LEFT -> Coordinate(
                    top = getDistanceToMiddleOfTank(
                        tankLeftTopCoordinate.top, bullet.layoutParams.height
                    ),
                    left = tankLeftTopCoordinate.left - bullet.layoutParams.width
                )

            Direction.RIGHT -> Coordinate(
                    top = getDistanceToMiddleOfTank(
                        tankLeftTopCoordinate.top, bullet.layoutParams.height
                    ),
                    left = tankLeftTopCoordinate.left + myTank.layoutParams.width
                )
            }
        }

    private fun getDistanceToMiddleOfTank(startCoordinate: Int, bulletSize: Int): Int{
        return startCoordinate + (CELL_SIZE - bulletSize / 2)
    }

}