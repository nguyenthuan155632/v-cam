package com.vcam.baker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FilterRecipesTest {
    @get:Rule val tmp = TemporaryFolder()

    @Test
    fun contains29Recipes() {
        assertEquals(29, FilterRecipes.all.size)
    }

    @Test
    fun recipeIdsAreStableAndUnique() {
        val ids = FilterRecipes.all.keys.toList()
        assertEquals(ids.size, ids.toSet().size)
        assertEquals("food_fresh", ids.first())
        assertEquals("mono_warm", ids.last())
    }

    @Test
    fun mainWritesAllCubeFilesAndReferenceImage() {
        val assets = tmp.newFolder("assets")
        main(arrayOf(assets.absolutePath))

        val cubes = assets.resolve("luts").listFiles { file -> file.extension == "cube" }?.map { it.name }?.sorted().orEmpty()
        assertEquals(29, cubes.size)
        assertTrue(cubes.contains("food_fresh.cube"))
        assertTrue(cubes.contains("mono_warm.cube"))
        assertTrue(assets.resolve("thumbs/reference.png").length() > 0)
    }
}
