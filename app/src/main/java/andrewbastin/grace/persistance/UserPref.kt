package andrewbastin.grace.persistance

import android.content.Context

class UserPref(context: Context): SharedPrefs("user", context) {

    var userSetup by booleanPref("bool_setup", false)
    var userName by stringPref("name_user", "<unnamed>")

}
