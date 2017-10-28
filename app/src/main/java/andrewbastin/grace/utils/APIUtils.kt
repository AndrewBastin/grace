package andrewbastin.grace.utils

import android.annotation.SuppressLint
import android.os.Build

fun isAPIAbove(version: Int) = Build.VERSION.SDK_INT > version

fun isAPIAboveOrIs(version: Int) = Build.VERSION.SDK_INT >= version

fun isAPIBelow(version: Int) = Build.VERSION.SDK_INT < version

fun isAPIBelowOrIs(version: Int) = Build.VERSION.SDK_INT <= version

fun isAPI(version: Int) = Build.VERSION.SDK_INT == version

fun <T: Any> aboveAPI(api: Int, inclusive: Boolean = false, func: () -> T): T? {

    if (inclusive) {
        if (Build.VERSION.SDK_INT >= api) return func()
    } else {
        if (Build.VERSION.SDK_INT > api) return func()
    }

    return null
}

fun <T: Any> untilAPI(api: Int, inclusive: Boolean = false, func: () -> T): T? {

    if (inclusive) {
        if (Build.VERSION.SDK_INT <= api) return func()
    } else {
        if (Build.VERSION.SDK_INT < api) return func()
    }

    return null
}

fun <T: Any> onAPI(api: Int, func: () -> T): T? {

    if (Build.VERSION.SDK_INT == api) return func()

    return null
}