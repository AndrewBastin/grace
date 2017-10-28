package andrewbastin.grace.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView

fun ImageView.getImageAsBitmap(): Bitmap? {
    try {
        isDrawingCacheEnabled = true
        measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        layout(0, 0, measuredWidth, measuredHeight)
        buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(drawingCache)
        isDrawingCacheEnabled = false

        return bitmap
    } catch (e: Exception) {
        return null
    }
}