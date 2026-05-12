package com.vcam.color

object FilterCatalog {
    val all: List<Filter> = listOf(
        Filter("food_fresh", FilterCategory.Food, "Fresh", "FD·1", "luts/food_fresh.cube"),
        Filter("food_sweet", FilterCategory.Food, "Sweet", "FD·2", "luts/food_sweet.cube"),
        Filter("food_warm_table", FilterCategory.Food, "Warm Table", "FD·3", "luts/food_warm_table.cube"),
        Filter("food_creamy", FilterCategory.Food, "Creamy", "FD·4", "luts/food_creamy.cube"),
        Filter("food_garden", FilterCategory.Food, "Garden", "FD·5", "luts/food_garden.cube"),
        Filter("portrait_clean_bright", FilterCategory.Portrait, "Clean Bright", "PR·1", "luts/portrait_clean_bright.cube"),
        Filter("portrait_soft_skin", FilterCategory.Portrait, "Soft Skin", "PR·2", "luts/portrait_soft_skin.cube"),
        Filter("portrait_warm", FilterCategory.Portrait, "Warm", "PR·3", "luts/portrait_warm.cube"),
        Filter("portrait_pink_tint", FilterCategory.Portrait, "Pink Tint", "PR·4", "luts/portrait_pink_tint.cube"),
        Filter("portrait_studio_glow", FilterCategory.Portrait, "Studio Glow", "PR·5", "luts/portrait_studio_glow.cube"),
        Filter("film_classic_cool", FilterCategory.Film, "Classic Cool", "FM·1", "luts/film_classic_cool.cube"),
        Filter("film_soft", FilterCategory.Film, "Soft Film", "FM·2", "luts/film_soft.cube"),
        Filter("film_warm_vintage", FilterCategory.Film, "Warm Vintage", "FM·3", "luts/film_warm_vintage.cube"),
        Filter("film_faded_negative", FilterCategory.Film, "Faded Negative", "FM·4", "luts/film_faded_negative.cube"),
        Filter("film_pushed_color", FilterCategory.Film, "Pushed Color", "FM·5", "luts/film_pushed_color.cube"),
        Filter("film_muted_frame", FilterCategory.Film, "Muted Frame", "FM·6", "luts/film_muted_frame.cube"),
        Filter("travel_summer_pop", FilterCategory.Travel, "Summer Pop", "TR·1", "luts/travel_summer_pop.cube"),
        Filter("travel_beach_bright", FilterCategory.Travel, "Beach Bright", "TR·2", "luts/travel_beach_bright.cube"),
        Filter("travel_city_clear", FilterCategory.Travel, "City Clear", "TR·3", "luts/travel_city_clear.cube"),
        Filter("travel_teal_orange", FilterCategory.Travel, "Teal Orange", "TR·4", "luts/travel_teal_orange.cube"),
        Filter("travel_golden_hour", FilterCategory.Travel, "Golden Hour", "TR·5", "luts/travel_golden_hour.cube"),
        Filter("night_city", FilterCategory.Night, "City Night", "NT·1", "luts/night_city.cube"),
        Filter("night_neon_soft", FilterCategory.Night, "Neon Soft", "NT·2", "luts/night_neon_soft.cube"),
        Filter("night_low_light_warm", FilterCategory.Night, "Low Light Warm", "NT·3", "luts/night_low_light_warm.cube"),
        Filter("night_moody_blue", FilterCategory.Night, "Moody Blue", "NT·4", "luts/night_moody_blue.cube"),
        Filter("mono_soft_bw", FilterCategory.Mono, "Soft B&W", "MN·1", "luts/mono_soft_bw.cube"),
        Filter("mono_high_contrast_bw", FilterCategory.Mono, "High Contrast B&W", "MN·2", "luts/mono_high_contrast_bw.cube"),
        Filter("mono_noir", FilterCategory.Mono, "Noir", "MN·3", "luts/mono_noir.cube"),
        Filter("mono_warm", FilterCategory.Mono, "Warm Mono", "MN·4", "luts/mono_warm.cube"),
    )

    fun byCategory(cat: FilterCategory): List<Filter> = all.filter { it.category == cat }

    fun byId(id: String): Filter? = all.firstOrNull { it.id == id }
}
