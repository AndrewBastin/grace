package andrewbastin.grace.utils

import android.graphics.Color

object ColorUtils {
    fun darken(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= factor
        return Color.HSVToColor(hsv)
    }

    fun getBlackWhiteContrast(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val lum = 0.299 * red + (0.587 * green + 0.114 * blue)
        return if (lum > 186) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
    }
}