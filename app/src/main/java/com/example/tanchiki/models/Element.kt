package com.example.tanchiki.models
import com.example.tanchiki.enums.Material

data class Element(
    val viewId:Int,
    val material: Material,
    val coordinate: Coordinate,
){

}
