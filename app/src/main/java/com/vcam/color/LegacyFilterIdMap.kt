package com.vcam.color

object LegacyFilterIdMap {
    private val map = mapOf(
        "fd01" to "food_fresh",
        "fd02" to "food_sweet",
        "fd03" to "food_warm_table",
        "cf01" to "food_creamy",
        "cf02" to "food_warm_table",
        "cf03" to "food_garden",
        "pr01" to "portrait_clean_bright",
        "pr02" to "portrait_soft_skin",
        "pr03" to "portrait_warm",
        "tr01" to "travel_summer_pop",
        "tr02" to "travel_beach_bright",
        "tr03" to "travel_golden_hour",
        "vt01" to "film_warm_vintage",
        "vt02" to "film_faded_negative",
        "vt03" to "film_muted_frame",
        "nt01" to "night_neon_soft",
        "nt02" to "night_moody_blue",
        "nt03" to "night_low_light_warm",
        "cl01" to "portrait_clean_bright",
        "cl02" to "portrait_studio_glow",
        "wm01" to "travel_golden_hour",
        "wm02" to "food_warm_table",
        "co01" to "film_classic_cool",
        "co02" to "night_city",
    )

    fun migrate(id: String): String {
        if (FilterCatalog.byId(id) != null) return id
        return map[id] ?: FilterCatalog.all.first().id
    }
}
