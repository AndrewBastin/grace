package andrewbastin.grace

import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.singletons.GraceHttpClient
import andrewbastin.grace.singletons.Prefs
import android.app.Application
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import com.afollestad.appthemeengine.ATE
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import kotlin.system.measureTimeMillis

// Generate all singletons here
class GraceApp : Application() {

    companion object {
        val IMAGE_CACHE_SIZE = (50 * 1024 * 1024).toLong()
        val IMAGE_CACHE_DIR_RELATIVE = "/cache/image"
    }

    override fun onCreate() {
        val timeTaken = measureTimeMillis {
            super.onCreate()

            PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

            Prefs.loadPrefs(this)

            GraceHttpClient.initialize(applicationContext)


            MusicPlayerManager.startMusicPlayerService(applicationContext)

            generatePicassoSingleton()

            ATE.config(this, null).apply {

                activityTheme( if (Prefs.ThemePref.darkMode) R.style.AppTheme_Dark else R.style.AppTheme )
                toolbarColor(Prefs.ThemePref.primaryColor)
                primaryColor(Prefs.ThemePref.primaryColor)
                accentColor(Prefs.ThemePref.accentColor)
                statusBarColor(Color.TRANSPARENT)

            }.commit()
        }
        Log.i("GraceApp", "Loaded Application in $timeTaken ms")
    }

    private fun generatePicassoSingleton() {
        val cache = Cache(File("${applicationContext.applicationInfo.dataDir}$IMAGE_CACHE_DIR_RELATIVE"), IMAGE_CACHE_SIZE)
        val picassoBuilder = Picasso.Builder(applicationContext)
        picassoBuilder.downloader(OkHttp3Downloader(OkHttpClient.Builder().cache(cache).build()))
        Picasso.setSingletonInstance(picassoBuilder.build())
    }

}