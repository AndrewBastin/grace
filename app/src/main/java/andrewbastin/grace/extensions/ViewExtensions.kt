package andrewbastin.grace.extensions

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

fun <T: View> View.bind(@IdRes res: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE){ findViewById<T>(res) }