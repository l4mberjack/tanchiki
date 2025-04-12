package com.example.tanchiki

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.FrameLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
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

const val CELL_SIZE = 50

lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private var editMode = false

    private val playerTank = Tank(
        Element(
            R.id.myTank,
            Material.PLAYER_TANK,
            Coordinate(0,0),
        ),UP
    )

    private val gridDrawer by lazy{
        GridDrawer(binding.container)
    }

    private val elementsDrawer by lazy{
        ElementsDrawer(binding.container)
    }

    private val bulletDrawer by lazy{
        BulletDrawer(binding.container)
    }

    private val levelStorage by lazy{
        LevelStorage(this)
    }

    private val enemyDrawer by lazy {
        EnemyDrawer(binding.container, elementsDrawer.elementsOnContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Menu"

        binding.editorClear.setOnClickListener{ elementsDrawer.currentMaterial = Material.EMPTY }
        binding.editorBrick.setOnClickListener{ elementsDrawer.currentMaterial = Material.BRICK }
        binding.editorConcrete.setOnClickListener{ elementsDrawer.currentMaterial = Material.CONCRETE }
        binding.editorGrass.setOnClickListener{ elementsDrawer.currentMaterial = Material.GRASS }
        binding.editorEagle.setOnClickListener{ elementsDrawer.currentMaterial = Material.EAGLE }


        binding.container.setOnTouchListener{ _, event->
            elementsDrawer.onTouchContainer(event.x,event.y)
            return@setOnTouchListener true
        }
        elementsDrawer.drawElementsList(levelStorage.loadLevel())
        hideSettings()
        elementsDrawer.elementsOnContainer.add(playerTank.element)
    }

    private fun switchEditMode(){
        if(editMode){
            showSettings()
        }
        else{
            hideSettings()
        }
    }

    private fun showSettings(){
        gridDrawer.drawGrid()
        binding.materialsContainer.visibility = VISIBLE
    }

    private fun hideSettings(){
        gridDrawer.removeGrid()
        binding.materialsContainer.visibility = INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
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
                startTheGame()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startTheGame() {
        if(editMode){
            return
        }
        enemyDrawer.startEnemyCreation()
        enemyDrawer.moveEnemyTanks()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode){
            KEYCODE_DPAD_UP -> move(UP)
            KEYCODE_DPAD_DOWN -> move(DOWN)
            KEYCODE_DPAD_LEFT -> move(LEFT)
            KEYCODE_DPAD_RIGHT -> move(RIGHT)
            KEYCODE_SPACE -> bulletDrawer.makeBulletMove(
                binding.myTank,
                playerTank.direction,
                elementsDrawer.elementsOnContainer
            )
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun Tank.move(direction: Direction){
        when(direction){
            UP->{
                binding.myTank.rotation = 0f
                if(binding.myTank.marginTop > 0) {
                    (binding.myTank.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
                }
            }
            DOWN -> {
                binding.myTank.rotation = 180f
                if(binding.myTank.marginTop+ binding.myTank.height < binding.container.height / CELL_SIZE * CELL_SIZE) {
                    (binding.myTank.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
                }

            }
            LEFT -> {
                binding.myTank.rotation = 270f
                if(binding.myTank.marginLeft > 0) {
                    (binding.myTank.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
                }
            }
            RIGHT ->{
                binding.myTank.rotation = 90f
                if(binding.myTank.marginLeft+ binding.myTank.width < binding.container.width / CELL_SIZE * CELL_SIZE) {
                (binding.myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
                }
            }
        }
        binding.container.removeView(binding.myTank)
        binding.container.addView(binding.myTank)
    }
    private fun move(direction: Direction){
        playerTank.move(direction, binding.container, elementsDrawer.elementsOnContainer)
    }
}