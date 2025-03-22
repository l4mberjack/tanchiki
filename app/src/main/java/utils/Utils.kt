package utils

import android.view.View
import com.example.tanchiki.binding
import com.example.tanchiki.models.Coordinate

fun View.checkTankCanMoveThroughBorder(coordinate: Coordinate): Boolean{
    return coordinate.top >= 0 &&
            coordinate.top + this.height <= binding.container.height &&
            coordinate.left >= 0 &&
            coordinate.left + this.width <= binding.container.width
}
