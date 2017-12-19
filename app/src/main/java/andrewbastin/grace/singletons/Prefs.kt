package andrewbastin.grace.singletons

import andrewbastin.grace.persistance.prefs.ThemePref
import andrewbastin.grace.persistance.prefs.UserPref
import android.content.Context

object Prefs {

    lateinit var ThemePref: ThemePref
        private set

    lateinit var UserPref: UserPref
        private set

    fun loadPrefs(context: Context) {
        ThemePref = ThemePref(context)
        UserPref = UserPref(context)
    }


}