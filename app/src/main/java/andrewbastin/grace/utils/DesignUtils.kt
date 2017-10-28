package andrewbastin.grace.utils

import android.content.Context
import android.os.Build

object DesignUtils {

    fun dpFromPixels(context: Context, px: Float) = px / context.resources.displayMetrics.density

    fun pixelsFromDP(context: Context, dp: Float) = dp * context.resources.displayMetrics.density

    fun getStatusBarHeight(context: Context): Int {

        val resources = context.resources
        val resourceID = resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resourceID > 0) {
            return resources.getDimensionPixelSize(resourceID)
        } else {
            return Math.ceil(((if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25) * resources.displayMetrics.density).toDouble()).toInt()
        }

    }

}