package com.vcam.data

import androidx.compose.ui.graphics.Color

data class Filter(
    val id: String,
    val category: String,
    val name: String,
    val code: String,
    val lutAsset: String,
    val tintTop: Color,
    val tintBottom: Color,
    val saturate: Float = 1f,
    val contrast: Float = 1f,
    val brightness: Float = 1f,
    val sepia: Float = 0f,
    val hueRotateDeg: Float = 0f,
)
