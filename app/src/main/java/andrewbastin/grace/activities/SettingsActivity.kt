package andrewbastin.grace.activities

import andrewbastin.grace.R
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.singletons.Prefs
import andrewbastin.grace.utils.ColorUtils
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.ATEActivity
import com.afollestad.appthemeengine.Config
import com.afollestad.appthemeengine.customizers.ATEStatusBarCustomizer
import com.kizitonwose.colorpreference.ColorPreference
import com.pes.androidmaterialcolorpickerdialog.ColorPicker

class SettingsActivity : ATEActivity(), ATEStatusBarCustomizer {

    val toolBar: Toolbar by bind(R.id.settingsActivityToolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!ATE.config(this, null).isConfigured) {
            ATE.config(this, null).apply {

                activityTheme(if (Prefs.ThemePref.darkMode) R.style.AppTheme_Dark else R.style.AppTheme)
                toolbarColor(Prefs.ThemePref.primaryColor)
                primaryColor(Prefs.ThemePref.primaryColor)
                accentColor(Prefs.ThemePref.accentColor)
                statusBarColor(Color.TRANSPARENT)

            }.commit()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fragmentManager.beginTransaction().replace(R.id.settingsActivitySettingsPlaceholder, SettingsActivityFragment()).commit()
    }

    override fun getLightStatusBarMode() = Config.LIGHT_STATUS_BAR_AUTO

    override fun getStatusBarColor() = ColorUtils.darken(Prefs.ThemePref.primaryColor, 0.8f)

    override fun onSupportNavigateUp(): Boolean {
        this.finish() // Closes the activity
        return true
    }
}

class SettingsActivityFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private val CONFIG_BOOL_DARK = "bool_dark"
        private val CONFIG_COLOR_ACCENT = "color_accent"
        private val CONFIG_COLOR_PRIMARY = "color_primary"

        private val CONFIG_TESTING_TESTER_NAME = "testing_tester_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        PreferenceManager.getDefaultSharedPreferences(activity)
                .registerOnSharedPreferenceChangeListener(this)

        findPreference(CONFIG_COLOR_ACCENT).setOnPreferenceClickListener {
            onColorConfigClick(it, Prefs.ThemePref.accentColor)
        }

        findPreference(CONFIG_COLOR_PRIMARY).setOnPreferenceClickListener {
            onColorConfigClick(it, Prefs.ThemePref.primaryColor)
        }

        (findPreference(CONFIG_TESTING_TESTER_NAME) as EditTextPreference).text = Prefs.UserPref.userName
    }

    fun onColorConfigClick(pref: Preference, fallbackColor: Int): Boolean {
        if (pref is ColorPreference) {
            val color = PreferenceManager.getDefaultSharedPreferences(activity).getInt(pref.key, fallbackColor)

            val colorPicker = ColorPicker(activity, Color.red(color), Color.green(color), Color.blue(color))

            colorPicker.setCallback {
                pref.value = it
            }

            colorPicker.show()

        }

        return true
    }

    override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {

        try {
            if (sharedPref != null) {

                when (key) {

                    CONFIG_BOOL_DARK -> {
                        Prefs.ThemePref.darkMode = sharedPref.getBoolean(key, Prefs.ThemePref.darkMode)
                        ATE.config(activity, null).activityTheme(if (Prefs.ThemePref.darkMode) R.style.AppTheme_Dark else R.style.AppTheme ).commit()
                    }
                    CONFIG_COLOR_PRIMARY -> {
                        Prefs.ThemePref.primaryColor = sharedPref.getInt(key, Prefs.ThemePref.primaryColor)
                        ATE.config(activity, null).primaryColor(Prefs.ThemePref.primaryColor).commit()
                    }
                    CONFIG_COLOR_ACCENT -> {
                        Prefs.ThemePref.accentColor = sharedPref.getInt(key, Prefs.ThemePref.accentColor)
                        ATE.config(activity, null).accentColor(Prefs.ThemePref.accentColor).commit()
                    }

                    CONFIG_TESTING_TESTER_NAME -> {
                        Prefs.UserPref.userName = sharedPref.getString(key, Prefs.UserPref.userName)
                    }

                }
            }
        } catch (e: Exception) {
            // TODO : Implement App restart mechanism
            Log.e("GraceApp/ThemeEngine", "Engine failed to change colors")
        }

    }
}