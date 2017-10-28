@file:Suppress("UNCHECKED_CAST")

package andrewbastin.grace.extensions

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View

fun <T: View> Activity.bind(@IdRes res: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE){ findViewById<T>(res) }

fun <T: Fragment> AppCompatActivity.bindFragmentCompat(@IdRes res: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {supportFragmentManager.findFragmentById(res) as T }