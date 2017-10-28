package andrewbastin.grace.views

import andrewbastin.grace.utils.DesignUtils
import android.content.Context
import android.util.AttributeSet
import android.view.View

class StatusBarTranslucencyView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr:Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isInEditMode) {

            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSize = DesignUtils.getStatusBarHeight(context)

            setMeasuredDimension(widthSize, heightSize)

        } else {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSize = 84 // Mock 25 dp

            setMeasuredDimension(widthSize, heightSize)
        }
    }
}