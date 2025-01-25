package com.example.tanchiki.drawers

import android.net.TelephonyNetworkSpecifier
import android.widget.FrameLayout
import android.view.View
import android.widget.ImageView
import com.example.tanchiki.CELL_SIZE
import com.example.tanchiki.R
import com.example.tanchiki.enums.Material
import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.models.Element

class ElementsDrawer (val container: FrameLayout) {
    var currentMaterial = Material.EMPTY
    private val elementsOnContainer = mutableListOf<Element>()

    fun onTouchContainer(x: Float, y:Float){
        val topMargin = y.toInt() - (y.toInt() % CELL_SIZE)
        val leftMargin = x.toInt() - (x.toInt() % CELL_SIZE)
        val coordinate = Coordinate(topMargin, leftMargin)
        drawView(coordinate)
    }

     fun drawView(coordinate: Coordinate){
        val view = ImageView(container.context)
        val layoutParams = FrameLayout.LayoutParams(CELL_SIZE, CELL_SIZE)
        when(currentMaterial){
            Material.EMPTY->{

            }

            Material.BRICK->view.setImageResource(R.drawable.brick)
            Material.CONCRETE->view.setImageResource(R.drawable.concrete)
            Material.GRASS->view.setImageResource(R.drawable.concrete)
        }
        layoutParams.topMargin = coordinate.top
        layoutParams.leftMargin = coordinate.left
        val viewId = View.generateViewId()
        view.id = viewId
        view.layoutParams = layoutParams
        container.addView(view)
        elementsOnContainer.add(Element(viewId,currentMaterial,coordinate))
    }

}