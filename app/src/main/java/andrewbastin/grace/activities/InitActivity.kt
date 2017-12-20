package andrewbastin.grace.activities

import android.app.Activity
import android.os.Bundle
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.singletons.Prefs
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

class InitActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if permissions are granted
        val storagePermStat = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)

        val intent = if (storagePermStat == PackageManager.PERMISSION_GRANTED && Prefs.UserPref.userSetup) {
            MusicCollection.loadMediaStoreData(applicationContext.contentResolver)

            Intent(this, MainActivity::class.java)
        } else {

            Intent(this,  IntroActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
