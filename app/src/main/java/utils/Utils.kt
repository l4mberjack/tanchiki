package utils

import android.view.View
import com.example.tanchiki.CELL_SIZE
import com.example.tanchiki.binding
import com.example.tanchiki.models.Coordinate
import com.example.tanchiki.models.Element

fun View.checkTankCanMoveThroughBorder(coordinate: Coordinate): Boolean{
    return coordinate.top >= 0 &&
            coordinate.top + this.height <= binding.container.height &&
            coordinate.left >= 0 &&
            coordinate.left + this.width <= binding.container.width
}

fun getElementByCoordinates(
    coordinate: Coordinate,
    elementsOnContainer: List<Element>
): Element? {
    for (element in elementsOnContainer) {
        for (height in 0  until element.height) {
            for (width in 0 until element.width){
                val searchCoordinate = Coordinate(
                    top = element.coordinate.top + height + CELL_SIZE,
                    left = element.coordinate.left + width + CELL_SIZE
                )
                if(coordinate == searchCoordinate){
                    return element
                }
            }
        }
    }
    return null
}
