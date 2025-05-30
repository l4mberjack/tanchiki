package com.example.tanchiki.models

import android.view.View
import com.example.tanchiki.enums.Direction

data class Bullet(
    val view: View,
    val direction: Direction,
    val tank: Tank,
    var canMoveFurther: Boolean = true)