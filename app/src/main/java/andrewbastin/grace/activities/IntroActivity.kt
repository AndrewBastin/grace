package andrewbastin.grace.activities

import android.os.Bundle
import andrewbastin.grace.R
import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.SlideFragmentBuilder
import andrewbastin.grace.fragments.IntroNameFragment
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.singletons.Prefs
import andrewbastin.grace.utils.aboveAPI
import android.Manifest
import android.content.Intent
import android.os.Build


class IntroActivity : MaterialIntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableLastSlideAlphaExitTransition(true)

        backButtonTranslationWrapper.setEnterTranslation { view, percentage -> view.alpha = percentage }

        addSlide (
                SlideFragmentBuilder().apply {
                    backgroundColor(R.color.branding_logo_bg)
                    buttonsColor(R.color.branding_logo_bg_dark)
                    image(R.drawable.ic_launcher_web)
                    title("Welcome")
                    description("Thanks for helping testing out Grace")
                }.build()
        )

        addSlide (
                IntroNameFragment()
        )

        aboveAPI(Build.VERSION_CODES.M, true) {
            addSlide(
                    SlideFragmentBuilder().apply {
                        backgroundColor(R.color.branding_logo_bg)
                        buttonsColor(R.color.branding_logo_bg_dark)
                        image(R.drawable.ic_folder)
                        neededPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                        title("Permissions")
                        description("Hey, we need your permission to access the songs. Please grant access to continue")
                    }.build()
            )
        }
    }

    override fun onFinish() {
        super.onFinish()

        MusicCollection.loadMediaStoreData(contentResolver)
        Prefs.UserPref.userSetup = true

        startActivity(Intent(this, MainActivity::class.java))
    }
}
