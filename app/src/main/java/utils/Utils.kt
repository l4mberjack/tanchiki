package utils

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
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

fun Element.drawElement(container: FrameLayout){
    val view = ImageView(container.context)
    val layoutParams = FrameLayout.LayoutParams(
        this.material.width * CELL_SIZE,
        this.material.height * CELL_SIZE
    )
    this.material.Image?.let { view.setImageResource(it) }
    layoutParams.topMargin = this.coordinate.top
    layoutParams.leftMargin = this.coordinate.left
    view.id = this.viewId
    view.layoutParams = layoutParams
    view.scaleType = ImageView.ScaleType.FIT_XY
    container.runOnUiThread{
        container.addView(view)
    }
}

fun FrameLayout.runOnUiThread(block:()-> Unit){
    (this.context as Activity).runOnUiThread{
        block()
    }

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
