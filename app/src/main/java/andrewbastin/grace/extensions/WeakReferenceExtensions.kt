package andrewbastin.grace.extensions

import java.lang.ref.WeakReference

fun <T : Any> WeakReference<T>.accessSafely(func: T.() -> Unit) {
    get()?.func()
}

fun <T: Any> WeakReference<T>.getSafely(func: (T) -> Unit) {
    val data = get()
    if (data != null) {
        func(data)
    }
}