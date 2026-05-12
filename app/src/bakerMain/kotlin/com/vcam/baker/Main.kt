package com.vcam.baker

import java.io.File

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Pass assets dir as first argument" }
    val assetsDir = File(args[0])
    require(assetsDir.isDirectory) { "Not a directory: $assetsDir" }

    val lutsDir = File(assetsDir, "luts").apply { mkdirs() }
    lutsDir.listFiles { file -> file.extension == "cube" }?.forEach { it.delete() }

    var count = 0
    FilterRecipes.all.forEach { (id, params) ->
        val lut = LutBaker.bake(params)
        LutBaker.writeCubeFile(id, lut, File(lutsDir, "$id.cube"))
        count++
    }
    println("[bakeLuts] wrote $count .cube files to $lutsDir")

    val refOut = File(assetsDir, "thumbs/reference.png")
    ReferenceImageBaker.bake(refOut)
    println("[bakeLuts] wrote reference image to $refOut")
}
