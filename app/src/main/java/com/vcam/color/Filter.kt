package com.vcam.color

data class Filter(
    val id: String,
    val category: FilterCategory,
    val displayName: String,
    val shortCode: String,
    val lutAsset: String,
    val intensityCap: Float = 1f,
)
