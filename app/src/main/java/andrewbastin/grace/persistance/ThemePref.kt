package andrewbastin.grace.persistance

import android.content.Context
import android.graphics.Color

class ThemePref(context: Context) : SharedPrefs("theme", context) {

    var darkMode by booleanPref("bool_dark", false)
    var primaryColor by intPref("color_primary", Color.parseColor("#3f51b5"))
    var accentColor by intPref("color_accent", Color.parseColor("#ff4081"))

}