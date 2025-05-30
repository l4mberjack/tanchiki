package com.example.tanchiki.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import com.example.tanchiki.GameCore
import com.example.tanchiki.LevelStorage
import com.example.tanchiki.R
import com.example.tanchiki.enums.Direction.UP
import com.example.tanchiki.enums.Direction.DOWN
import com.example.tanchiki.enums.Direction.LEFT
import com.example.tanchiki.enums.Direction.RIGHT
import com.example.tanchiki.databinding.ActivityMainBinding
import com.example.tanchiki.drawers.BulletDrawer
import com.example.tanchiki.drawers.ElementsDrawer
import com.example.tanchiki.drawers.EnemyDrawer
import com.example.tanchiki.drawers.GridDrawer
import com.example.tanchiki.enums.Direction
import com.example.tanchiki.enums.Material
import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.models.Element
import com.example.tanchiki.models.Tank
import com.example.tanchiki.sounds.MainSoundPlayer
import utils.ProgressIndicator
import kotlin.concurrent.thread

const val CELL_SIZE = 50

lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity(), ProgressIndicator  {
    private var editMode = false
    private lateinit var item: MenuItem

    private lateinit var playerTank: Tank
    private lateinit var eagle: Element

    private var gameStarted = false

    private fun createTank(elementWidth: Int, elementHeight: Int): Tank {
        playerTank = Tank(
            Element(
                material = Material.PLAYER_TANK,
                coordinate = getPlayerTankCoordinate(elementWidth, elementHeight),
            ), UP,
            enemyDrawer
        )
        return playerTank
    }

    private val gridDrawer by lazy {
        GridDrawer(binding.container)
    }

    private fun createEagle(elementWidth: Int, elementHeight: Int): Element {
        eagle = Element(
            material = Material.EAGLE,
            coordinate = getEagleCoordinate(elementWidth, elementHeight)
        )
        return eagle
    }

    private fun getPlayerTankCoordinate(width: Int, height: Int) = Coordinate (
        top = (height - height % 2)
                - (height - height % 2) % CELL_SIZE
                - Material.PLAYER_TANK.height * CELL_SIZE,
        left = (width - width % (2 * CELL_SIZE)) / 2
                - Material.EAGLE.width / 2 * CELL_SIZE
                - Material.PLAYER_TANK.width * CELL_SIZE
    )

    private fun getEagleCoordinate(width: Int, height: Int) = Coordinate(
        top = (height - height % 2)
                - (height - height % 2) % CELL_SIZE
                - Material.EAGLE.height * CELL_SIZE,
        left = (width - width % (2 * CELL_SIZE)) / 2
                - Material.EAGLE.width / 2 * CELL_SIZE
    )

    private val elementsDrawer by lazy{
        ElementsDrawer(binding.container)
    }

    private val levelStorage by lazy{
        LevelStorage(this)
    }

    private val enemyDrawer by lazy {
        EnemyDrawer(binding.container, elementsDrawer.elementsOnContainer, soundManager, gameCore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Menu"

        binding.editorClear.setOnClickListener{ elementsDrawer.currentMaterial = Material.EMPTY }
        binding.editorBrick.setOnClickListener{ elementsDrawer.currentMaterial = Material.BRICK }
        binding.editorConcrete.setOnClickListener{
            elementsDrawer.currentMaterial = Material.CONCRETE
        }
        binding.editorGrass.setOnClickListener{ elementsDrawer.currentMaterial = Material.GRASS }
        binding.container.setOnTouchListener{ _, event->
            if (!editMode) {
                return@setOnTouchListener true
            }
            elementsDrawer.onTouchContainer(event.x,event.y)
            return@setOnTouchListener true
        }
        elementsDrawer.drawElementsList(levelStorage.loadLevel())
        elementsDrawer.drawElementsList(listOf(playerTank.element,eagle))
        hideSettings()
        countWidthHeight()
    }

    private fun countWidthHeight() {
        val frameLayout = binding.container
        frameLayout.viewTreeObserver
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                   frameLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val elementWidth = frameLayout.width
                    val elementHeight = frameLayout.height

                    playerTank = createTank(elementWidth, elementHeight)
                    eagle = createEagle(elementWidth, elementHeight)

                    elementsDrawer.drawElementsList(listOf(playerTank.element, eagle))
                    enemyDrawer.bulletDrawer = bulletDrawer
                }
            })
    }

    private val bulletDrawer by lazy{
        BulletDrawer(
            binding.container,
            elementsDrawer.elementsOnContainer,
            enemyDrawer,
            soundManager,
            gameCore
        )
    }

    private val gameCore by lazy {
        GameCore(this)
    }

    private val soundManager by lazy {
        MainSoundPlayer(this, this)
    }
    private fun switchEditMode(){
        if(editMode){
            showSettings()
        }
        else{
            hideSettings()
        }
    }

    private fun showSettings() {
        gridDrawer.drawGrid() // рисуется сетка
        binding.materialsContainer.visibility = VISIBLE // отобр. контейнер с мат-ами
    }

    // метод для скрытия меню
    private fun hideSettings() {
        gridDrawer.removeGrid()
        binding.materialsContainer.visibility = INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        item = menu!!.findItem(R.id.menu_play)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_settings -> {
                switchEditMode()
                return true
        }
            R.id.menu_save -> {
                levelStorage.saveLevel(elementsDrawer.elementsOnContainer)
                return true
            }
            R.id.menu_play ->{
                if(editMode){
                    return true
                }
                showIntro()
                if(soundManager.areSoundsReady()){
                    gameCore.startOrPauseTheGame()
                    if(gameCore.isPlaying()){
                        resumeTheGame()
                    } else {
                        pauseTheGame()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resumeTheGame() {
        item.icon = ContextCompat.getDrawable(this, R.drawable.baseline_pause_24)
        gameCore.resumeTheGame()
    }

    private fun showIntro() {
        if(gameStarted){
            return
        }
        gameStarted = true
        soundManager.loadSounds()
    }

    private fun pauseTheGame() {
        item.icon = ContextCompat.getDrawable(this, R.drawable.play)
        gameCore.pauseTheGame()
        soundManager.pauseSounds()
    }

    override fun onPause() {
        super.onPause()
        pauseTheGame()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(!gameCore.isPlaying()){
            return super.onKeyDown(keyCode, event)
        }
        when (keyCode){
            KEYCODE_DPAD_UP -> move(UP)
            KEYCODE_DPAD_DOWN -> move(DOWN)
            KEYCODE_DPAD_LEFT -> move(LEFT)
            KEYCODE_DPAD_RIGHT -> move(RIGHT)
            KEYCODE_SPACE -> bulletDrawer.addNewBulletForTank(playerTank)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int,event: KeyEvent?):Boolean{
        if(!gameCore.isPlaying()){
            return super.onKeyUp(keyCode, event)
        }
        when(keyCode){
            KEYCODE_DPAD_UP, KEYCODE_DPAD_LEFT,
                KEYCODE_DPAD_DOWN, KEYCODE_DPAD_RIGHT -> onButtonReleased()
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun onButtonReleased() {
        if(enemyDrawer.tanks.isEmpty()){
            soundManager.tankStop()
        }
    }

    private fun onButtonPressed(direction: Direction){
        soundManager.tankMove()
        playerTank.move(direction, binding.container, elementsDrawer.elementsOnContainer)
    }


    private fun move(direction: Direction){
        playerTank.move(direction, binding.container, elementsDrawer.elementsOnContainer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == SCORE_REQUEST_CODE){
            recreate()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showProgress() {
        binding.container.visibility = INVISIBLE
        binding.totalContainer.setBackgroundResource(R.color.gray)
        binding.initTitle.visibility = VISIBLE
    }

    override fun dismissProgress() {
        Thread.sleep(3000L)
        binding.container.visibility = VISIBLE
        binding.totalContainer.setBackgroundResource(R.color.black)
        binding.initTitle.visibility = GONE
        enemyDrawer.startEnemyCreation()
        soundManager.playIntroMusic()
        resumeTheGame()
    }
}