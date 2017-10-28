package andrewbastin.grace.interfaces

interface BottomSheetEventListener {

    fun onSlide(slideOffset: Float)
    fun onSlideStateChanged(newState: Int)

}