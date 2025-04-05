package com.example.tanchiki.drawers

import androidx.core.content.contentValuesOf
import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.CELL_SIZE
import android.widget.FrameLayout

class EnemyDrawer(private val container:FrameLayout) {
    private val respawnList: List<Coordinate>

    init {
        respawnList = getRespawnList()
    }

    private fun getRespawnList(): List<Coordinate> {
        val respawnList = mutableListOf<Coordinate>()
        respawnList.add(Coordinate(0,0))
        respawnList.add(Coordinate(0,container.width / 2 - CELL_SIZE))
        respawnList.add(Coordinate(0,container.width - CELL_SIZE))
        return respawnList
    }

}