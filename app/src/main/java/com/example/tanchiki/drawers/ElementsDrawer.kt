package com.example.tanchiki.drawers

import android.widget.FrameLayout
import android.view.View
import com.example.tanchiki.activities.CELL_SIZE
import com.example.tanchiki.activities.binding
import com.example.tanchiki.enums.Direction
import com.example.tanchiki.enums.Material
import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.models.Element
import com.example.tanchiki.utils.drawElement
import com.example.tanchiki.utils.getElementByCoordinates


class ElementsDrawer (val container: FrameLayout) {
    var currentMaterial = Material.EMPTY
    val elementsOnContainer = mutableListOf<Element>()

    fun onTouchContainer(x: Float, y:Float){
        val topMargin = y.toInt() - (y.toInt() % CELL_SIZE)
        val leftMargin = x.toInt() - (x.toInt() % CELL_SIZE)
        val coordinate = Coordinate(topMargin, leftMargin)
        if (currentMaterial == Material.EMPTY){
            eraseView(coordinate)
        } else {
            drawOrReplaceView(coordinate)
        }
    }

    private fun drawOrReplaceView(coordinate: Coordinate) {
        val viewOnCoordinate = getElementByCoordinates(coordinate, elementsOnContainer)
        if (viewOnCoordinate == null){
            createElementDrawView(coordinate)
            return
        }
        if (viewOnCoordinate.material != currentMaterial) {
            replaceView(coordinate)
        }
    }

    private fun replaceView(coordinate: Coordinate){
        eraseView(coordinate)
        createElementDrawView(coordinate)
    }

    private fun eraseView(coordinate: Coordinate){
        getElementByCoordinates(coordinate, elementsOnContainer)?.let { removeElement(it) }
        for(element in getElementsUnderCurrentCoordinate(coordinate)){
            removeElement(element)
        }
    }

    private fun removeElement(element: Element) {
        if(element != null){
            val erasingView = container.findViewById<View>(element.viewId)
            container.removeView(erasingView)
            elementsOnContainer.remove(element)
        }
    }

    private fun removeUnwantedInstances() {
        if (currentMaterial.elementsAmountOnScreen != 0) {
            val erasingElements = elementsOnContainer.filter { it.material == currentMaterial }
            if (erasingElements.size >= currentMaterial.elementsAmountOnScreen) {
                eraseView(erasingElements[0].coordinate)
            }
        }
    }

    fun drawElementsList(elements: List<Element>?){
        if(elements == null){
            return
        }
        for(element in elements){
            currentMaterial = element.material
            drawElement(element)
        }
        currentMaterial = Material.EMPTY
    }

    private fun createElementDrawView(coordinate: Coordinate) {
        val element = Element(
            material = currentMaterial,
            coordinate = coordinate,
        )
        drawElement(element)
    }

    private fun drawElement(element: Element) {
        removeUnwantedInstances()
        element.drawElement(container)
        elementsOnContainer.add(element)
    }


    private fun getElementsUnderCurrentCoordinate(coordinate: Coordinate): List<Element>{
        val elements = mutableListOf<Element>()
        for(element in elementsOnContainer){
            for(height in 0 until currentMaterial.height){
                for(width in 0 until currentMaterial.width){
                 if(element.coordinate == Coordinate(
                         coordinate.top+height * CELL_SIZE,
                         coordinate.left + width * CELL_SIZE
                 )
                 ){
                     elements.add(element)
                 }
                }
            }
        }
        return elements
    }



    private fun checkTankCanMoveThroughMaterial(coordinate: Coordinate): Boolean{
        getTankCoordinates(coordinate).forEach {
            val element = getElementByCoordinates(coordinate, elementsOnContainer)
            if (element != null && !element.material.tankConGoThrough){
                return false
            }
        }
        return true
    }

    private fun checkTankCanMoveThroughBorder(coordinate: Coordinate, myTank: View): Boolean{
        return coordinate.top >= 0 &&
               coordinate.top + myTank.height <= binding.container.height &&
               coordinate.left >= 0 &&
               coordinate.left + myTank.width <= binding.container.width
    }

    private fun getTankCoordinates(topLeftCoordinate: Coordinate): List<Coordinate>{
        val coordinateList = mutableListOf<Coordinate>()
        coordinateList.add(topLeftCoordinate)
        coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left))
        coordinateList.add(Coordinate(topLeftCoordinate.top, topLeftCoordinate.left + CELL_SIZE))
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE,
                topLeftCoordinate.left + CELL_SIZE
            )
        )
        return coordinateList
    }
}