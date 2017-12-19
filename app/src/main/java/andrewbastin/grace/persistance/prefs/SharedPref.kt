package andrewbastin.grace.persistance.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

open class SharedPrefs(prefName: String, context: Context) {

    private var isBulkEditing = false
    protected val sharedPrefs: SharedPreferences = context.getSharedPreferences("andrewbastin.grace.pref:$prefName", Context.MODE_PRIVATE)
    protected val editor: SharedPreferences.Editor = sharedPrefs.edit()

    fun bulkApply(func: () -> Unit) {
        isBulkEditing = true
        func()
        isBulkEditing = false
        editor.apply()
    }

    fun bulkCommit(func: () -> Unit) {
        isBulkEditing = true
        func()
        isBulkEditing = false
        editor.commit()
    }

    fun stringPref(id: String, defaultValue: String) = StringPref(id, defaultValue)

    fun intPref(id: String, defaultValue: Int) = IntPref(id, defaultValue)

    fun booleanPref(id: String, defaultValue: Boolean) = BooleanPref(id, defaultValue)

    fun floatPref(id: String, defaultValue: Float) = FloatPref(id, defaultValue)

    fun longPref(id: String, defaultValue: Long) = LongPref(id, defaultValue)

    fun stringSetPref(id: String, defaultValue: Set<String>) = StringSetPref(id, defaultValue)



    inner class StringPref(val id: String, val defaultValue: String) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return sharedPrefs.getString(id, defaultValue)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
            editor.putString(id, newValue)
            if (!isBulkEditing) editor.apply()
        }

    }

    inner class IntPref(val id: String, val defaultValue: Int) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return sharedPrefs.getInt(id, defaultValue)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Int) {
            editor.putInt(id, newValue)
            if (!isBulkEditing) editor.apply()
        }

    }

    inner class BooleanPref(val id: String, val defaultValue: Boolean) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return sharedPrefs.getBoolean(id, defaultValue)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Boolean) {
            editor.putBoolean(id, newValue)
            if (!isBulkEditing) editor.apply()
        }

    }

    inner class FloatPref(val id: String, val defaultValue: Float) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
            return sharedPrefs.getFloat(id, defaultValue)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Float) {
            editor.putFloat(id, newValue)
            if (!isBulkEditing) editor.apply()
        }

    }

    inner class LongPref(val id: String, val defaultValue: Long) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return sharedPrefs.getLong(id, defaultValue)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Long) {
            editor.putLong(id, newValue)
            if (!isBulkEditing) editor.apply()
        }

    }

    inner class StringSetPref(val id: String, val defaultValue: Set<String>) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> {
            return sharedPrefs.getStringSet(id, defaultValue)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Set<String>) {
            editor.putStringSet(id, newValue)
            if (!isBulkEditing) editor.apply()
        }

    }
}