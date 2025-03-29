package com.example.tanchiki.enums

import com.example.tanchiki.R

const val CELLS_SIMPLE_ELEMENT = 1
const val CELLS_EAGLE_WIDTH = 4
const val CELLS_EAGLE_HEIGHT = 3

enum class Material(
    val tankConGoThrough: Boolean,
    val bulletCanGoThrough: Boolean,
    val simpleBulletCanDestroy: Boolean,
    val canExistOnlyOne: Boolean,
    val width: Int,
    val height: Int,
    val Image: Int
    ) {
    EMPTY(
        true,
        true,
        true,
        false,
        0,
        0,
        0
    ),
    BRICK(
        false,
        false,
        true,
        false,
        CELLS_SIMPLE_ELEMENT,
        CELLS_SIMPLE_ELEMENT,
        R.drawable.brick
    ),
    CONCRETE(
        false,
        false,
        false,
        false,
        1 ,
        1,
        R.drawable.concrete
    ),
    GRASS(
        true,
        true,
        false,
        false,
        1,
        1,
        R.drawable.grass
    ),
    EAGLE(
        false,
        false,
        true,
        true,
        4,
        3,
        R.drawable.eagle
    ),
}