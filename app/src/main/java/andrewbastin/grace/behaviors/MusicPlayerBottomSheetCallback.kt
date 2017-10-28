package andrewbastin.grace.behaviors

import andrewbastin.grace.extensions.accessSafely
import andrewbastin.grace.interfaces.BottomSheetEventListener
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import java.lang.ref.WeakReference

class MusicPlayerBottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {

    private val eventListeners: MutableList<WeakReference<BottomSheetEventListener>> = mutableListOf()

    fun addEventListener(eventListener: BottomSheetEventListener) {
        eventListeners.add(WeakReference(eventListener))
    }

    /**
     * Called when the bottom sheet is being dragged.

     * @param bottomSheet The bottom sheet view.
     * *
     * @param slideOffset The new offset of this bottom sheet within [-1,1] range. Offset
     * *                    increases as this bottom sheet is moving upward. From 0 to 1 the sheet
     * *                    is between collapsed and expanded states and from -1 to 0 it is
     * *                    between hidden and collapsed states.
     */
    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        eventListeners.forEach {
            it.accessSafely {
                onSlide(slideOffset)
            }
        }
    }

    /**
     * Called when the bottom sheet changes its state.

     * @param bottomSheet The bottom sheet view.
     * *
     * @param newState    The new state. This will be one of [.STATE_DRAGGING],
     * *                    [.STATE_SETTLING], [.STATE_EXPANDED],
     * *                    [.STATE_COLLAPSED], or [.STATE_HIDDEN].
     */
    override fun onStateChanged(bottomSheet: View, newState: Int) {
        eventListeners.forEach {
            it.accessSafely {
                onSlideStateChanged(newState)
            }
        }
    }
}