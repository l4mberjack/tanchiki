package com.example.tanchiki.drawers

import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.activities.CELL_SIZE
import android.widget.FrameLayout
import com.example.tanchiki.GameCore
import com.example.tanchiki.sounds.MainSoundPlayer
import com.example.tanchiki.enums.CELLS_TANKS_SIZE
import com.example.tanchiki.enums.Direction
import com.example.tanchiki.enums.Material
import com.example.tanchiki.models.Element
import com.example.tanchiki.models.Tank
import utils.checkIfChanceBiggerThanRandom
import utils.drawElement

private const val MAX_ENEMY_AMOUNT = 20

class EnemyDrawer(
    private val container: FrameLayout,
    private val elements: MutableList<Element>,
    private val soundManager: sounds.SoundManager,
    private val gameCore: GameCore
    ) {
    private val respawnList: List<Coordinate>
    private var enemyAmount = 0
    private var currentCoordinate:Coordinate
    val tanks = mutableListOf<Tank>()
    lateinit var bulletDrawer: BulletDrawer
    private var gameStarted = false
    private var enemyMurders = 0

    init {
        respawnList = getRespawnList()
        currentCoordinate = respawnList[0]
    }

    private fun getRespawnList(): List<Coordinate> {
        val respawnList = mutableListOf<Coordinate>()
        respawnList.add(Coordinate(0,0))
        respawnList.add(
            Coordinate(
                0,
                ((container.width - container.width % CELL_SIZE) / CELL_SIZE -
                        (container.width - container.width % CELL_SIZE) / CELL_SIZE % 2) *
                    CELL_SIZE / 2 - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        respawnList.add(
            Coordinate(
                0,
                (container.width - container.width % CELL_SIZE) - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        return respawnList
    }

    private fun drawEnemy() {
        var index = respawnList.indexOf(currentCoordinate) + 1
        if(index == respawnList.size) {
            index = 0
        }
        currentCoordinate = respawnList[index]
        val enemyTank = Tank(
            Element(
            material = Material.ENEMY_TANK,
            coordinate = currentCoordinate,
            ), Direction.DOWN,
            this
        )
        enemyTank.element.drawElement(container)
        tanks.add(enemyTank)
    }

    private fun moveEnemyTanks() {
        Thread(Runnable {
            while (true) {
                if(!gameCore.isPlaying()){
                    continue
                }
                goThroughAllTanks()
                Thread.sleep(400)
            }
        }).start()
    }

    private fun goThroughAllTanks() {
        if (tanks.isNotEmpty()){
            sounds.SoundManager.tankMove()
        } else {
            sounds.SoundManager.tankStop()
        }
            tanks.toList().forEach{
                it.move(it.direction, container, elements)
                if (checkIfChanceBiggerThanRandom(10)) {
                    bulletDrawer.addNewBulletForTank(it)
                }
            }
    }

    fun startEnemyCreation() {
        if(gameStarted){
            return
        }
        gameStarted = true
        Thread(Runnable {
            while (enemyAmount < MAX_ENEMY_AMOUNT) {
                if(!gameCore.isPlaying()){
                    continue
                }
                drawEnemy()
                enemyAmount++
                Thread.sleep(3000)
            }
        }).start()
        moveEnemyTanks()
    }

    private fun isAllTanksDestroyed(): Boolean{
        return enemyMurders == MAX_ENEMY_AMOUNT
    }

    fun getPlayerScore() = enemyMurders * 100

    fun removeTank(tankIndex: Int) {
        tanks.removeAt(tankIndex)
        enemyMurders++
        if (isAllTanksDestroyed()){
            gameCore.playerWon(getPlayerScore())
        }
    }

}