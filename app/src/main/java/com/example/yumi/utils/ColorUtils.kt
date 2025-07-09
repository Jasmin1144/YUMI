package com.example.yumi.utils

import android.graphics.Color

object ColorUtils {
    /**
     * Parses a color string. Returns a default color if parsing fails.
     *
     * @param colorString The color string to parse (e.g., "#RRGGBB", "#AARRGGBB").
     * @param defaultColor The color to return if parsing fails (defaults to Color.BLACK).
     * @return The parsed color or the default color.
     */
    fun parseColor(colorString: String?, defaultColor: Int = Color.BLACK): Int {
        if (colorString.isNullOrEmpty()) {
            return defaultColor
        }
        return try {
            Color.parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            // Log an error or handle it as needed
            // For now, return default color
            defaultColor
        }
    }
}
