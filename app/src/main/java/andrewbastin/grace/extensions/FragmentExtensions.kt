@file:Suppress("UNCHECKED_CAST")

package andrewbastin.grace.extensions

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.View

fun <T: View> Fragment.bind(res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        view!!.findViewById<T>(res)
    }
}