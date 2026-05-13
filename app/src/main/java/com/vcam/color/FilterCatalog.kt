package com.vcam.color

object FilterCatalog {
    val all: List<Filter> = listOf(
        Filter("food_fresh", FilterCategory.Food, "Fresh", "FD·1", "luts/food_fresh.cube", defaultIntensity = 0.85f),
        Filter("food_sweet", FilterCategory.Food, "Sweet", "FD·2", "luts/food_sweet.cube", defaultIntensity = 0.85f),
        Filter("food_warm_table", FilterCategory.Food, "Warm Table", "FD·3", "luts/food_warm_table.cube", defaultIntensity = 0.85f),
        Filter("food_creamy", FilterCategory.Food, "Creamy", "FD·4", "luts/food_creamy.cube", defaultIntensity = 0.85f),
        Filter("food_garden", FilterCategory.Food, "Garden", "FD·5", "luts/food_garden.cube", defaultIntensity = 0.85f),

        Filter("portrait_clean_bright", FilterCategory.Portrait, "Clean Bright", "PR·1", "luts/portrait_clean_bright.cube", defaultIntensity = 0.75f),
        Filter("portrait_soft_skin", FilterCategory.Portrait, "Soft Skin", "PR·2", "luts/portrait_soft_skin.cube", defaultIntensity = 0.75f),
        Filter("portrait_warm", FilterCategory.Portrait, "Warm", "PR·3", "luts/portrait_warm.cube", defaultIntensity = 0.75f),
        Filter("portrait_pink_tint", FilterCategory.Portrait, "Pink Tint", "PR·4", "luts/portrait_pink_tint.cube", defaultIntensity = 0.75f),
        Filter("portrait_studio_glow", FilterCategory.Portrait, "Studio Glow", "PR·5", "luts/portrait_studio_glow.cube", defaultIntensity = 0.75f),

        Filter("film_classic_cool", FilterCategory.Film, "Classic Cool", "FM·1", "luts/film_classic_cool.cube", defaultIntensity = 0.80f),
        Filter("film_soft", FilterCategory.Film, "Soft Film", "FM·2", "luts/film_soft.cube", defaultIntensity = 0.90f),
        Filter("film_warm_vintage", FilterCategory.Film, "Warm Vintage", "FM·3", "luts/film_warm_vintage.cube", defaultIntensity = 0.75f),
        Filter("film_faded_negative", FilterCategory.Film, "Faded Negative", "FM·4", "luts/film_faded_negative.cube", defaultIntensity = 0.85f),
        Filter("film_pushed_color", FilterCategory.Film, "Pushed Color", "FM·5", "luts/film_pushed_color.cube", defaultIntensity = 0.70f),
        Filter("film_muted_frame", FilterCategory.Film, "Muted Frame", "FM·6", "luts/film_muted_frame.cube", defaultIntensity = 0.90f),

        Filter("travel_summer_pop", FilterCategory.Travel, "Summer Pop", "TR·1", "luts/travel_summer_pop.cube", defaultIntensity = 0.80f),
        Filter("travel_beach_bright", FilterCategory.Travel, "Beach Bright", "TR·2", "luts/travel_beach_bright.cube", defaultIntensity = 0.80f),
        Filter("travel_city_clear", FilterCategory.Travel, "City Clear", "TR·3", "luts/travel_city_clear.cube", defaultIntensity = 0.80f),
        Filter("travel_teal_orange", FilterCategory.Travel, "Teal Orange", "TR·4", "luts/travel_teal_orange.cube", defaultIntensity = 0.80f),
        Filter("travel_golden_hour", FilterCategory.Travel, "Golden Hour", "TR·5", "luts/travel_golden_hour.cube", defaultIntensity = 0.80f),

        Filter("night_city", FilterCategory.Night, "City Night", "NT·1", "luts/night_city.cube", defaultIntensity = 0.75f),
        Filter("night_neon_soft", FilterCategory.Night, "Neon Soft", "NT·2", "luts/night_neon_soft.cube", defaultIntensity = 0.75f),
        Filter("night_low_light_warm", FilterCategory.Night, "Low Light Warm", "NT·3", "luts/night_low_light_warm.cube", defaultIntensity = 0.75f),
        Filter("night_moody_blue", FilterCategory.Night, "Moody Blue", "NT·4", "luts/night_moody_blue.cube", defaultIntensity = 0.75f),

        Filter("mono_soft_bw", FilterCategory.Mono, "Soft B&W", "MN·1", "luts/mono_soft_bw.cube", defaultIntensity = 0.90f),
        Filter("mono_high_contrast_bw", FilterCategory.Mono, "High Contrast B&W", "MN·2", "luts/mono_high_contrast_bw.cube", defaultIntensity = 0.90f),
        Filter("mono_noir", FilterCategory.Mono, "Noir", "MN·3", "luts/mono_noir.cube", defaultIntensity = 0.90f),
        Filter("mono_warm", FilterCategory.Mono, "Warm Mono", "MN·4", "luts/mono_warm.cube", defaultIntensity = 0.90f),
    )

    fun byCategory(cat: FilterCategory): List<Filter> = all.filter { it.category == cat }

    fun byId(id: String): Filter? = all.firstOrNull { it.id == id }
}
