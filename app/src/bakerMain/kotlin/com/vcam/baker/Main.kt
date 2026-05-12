package com.vcam.baker

import java.io.File

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Pass assets dir as first argument" }
    val assetsDir = File(args[0])
    require(assetsDir.isDirectory) { "Not a directory: $assetsDir" }

    val thumbsDir = File(assetsDir, "thumbs")
    thumbsDir.mkdirs()
    File(thumbsDir, "reference.png").writeBytes(ByteArray(0))

    println("[bakeLuts] (placeholder) assetsDir=$assetsDir")
}
