package com.example.tanchiki.models
import android.view.View
import android.widget.FrameLayout
import com.example.tanchiki.enums.Material

data class Element constructor(
    val viewId:Int = View.generateViewId(),
    val material: Material,
    var coordinate: Coordinate,
    val width: Int,
    val height: Int
){

}
