package andrewbastin.grace.activities

import andrewbastin.grace.R
import andrewbastin.grace.adapters.ViewPagerAdapter
import andrewbastin.grace.behaviors.MusicPlayerBottomSheetCallback
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.extensions.bindFragmentCompat
import andrewbastin.grace.fragments.*
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.singletons.Prefs
import andrewbastin.grace.utils.ColorUtils
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.isAPIBelow
import andrewbastin.grace.utils.untilAPI
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.ATEActivity
import kotlin.system.measureTimeMillis

class MainActivity : ATEActivity() {

    companion object {
        val ID_REQUEST_SHOW_MEDIA_PLAYER = 10000
        // TODO : Implement handling this request
    }

    private var musicPlayerBottomSheetCallback: MusicPlayerBottomSheetCallback? = null
    private var musicPlayerBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val musicPlayerFragment: MusicPlayerFragment by bindFragmentCompat(R.id.mainPageMusicPlayer)

    private val musicShuffleButton: ImageButton by bind(R.id.mainPageShuffleButton)
    private val statusBar: View by bind(R.id.statusBar)
    private val drawerLayout: DrawerLayout by bind(R.id.mainActivityDrawerLayout)
    private val drawer: NavigationView by bind(R.id.mainActivityDrawer)

    override fun onCreate(savedInstanceState: Bundle?) {
        val timeTaken = measureTimeMillis {
            super.onCreate(savedInstanceState)

            if (!ATE.config(this, null).isConfigured) {
                ATE.config(this, null).apply {

                    activityTheme( if (Prefs.ThemePref.darkMode) R.style.AppTheme_Dark else R.style.AppTheme )
                    toolbarColor(Prefs.ThemePref.primaryColor)
                    primaryColor(Prefs.ThemePref.primaryColor)
                    accentColor(Prefs.ThemePref.accentColor)
                    statusBarColor(Color.TRANSPARENT)

                }.commit()
            }

            setContentView(R.layout.activity_main)

            setupToolbar()

            // Music Player stuff
            MusicPlayerManager.addMusicPlayerListener(musicPlayerFragment)

            // View Pager stuff
            val viewPager by bind<ViewPager>(R.id.mainPageViewPager)
            val tabLayout by bind<TabLayout>(R.id.mainPageTabLayout)
            setupViewPager(viewPager)
            tabLayout.setupWithViewPager(viewPager)

            setupBottomSlideLayout()
            setupStylesForCompat()

            drawer.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menuMainActionSettings -> {
                        drawerLayout.closeDrawers()
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                }

                true
            }

            drawerLayout.closeDrawers()

        }

        Log.i("GraceApp/MainActivity", "Loading Activity took $timeTaken ms")
    }

    fun setupStylesForCompat() {
        // Borderless Ripple for Shuffle button on App Bar
        // VERSION >= MARSHMALLOW use borderless else use normal
        aboveAPI(Build.VERSION_CODES.M, inclusive = true) {
            val attrs = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
            val typedArray = obtainStyledAttributes(attrs)
            val drawable = typedArray.getDrawable(0)
            typedArray.recycle()
            musicShuffleButton.foreground = drawable

            statusBar.foreground = ColorDrawable(ColorUtils.darken(Prefs.ThemePref.primaryColor, 0.8f))
        }
        untilAPI(Build.VERSION_CODES.LOLLIPOP) {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val typedArray = obtainStyledAttributes(attrs)
            val drawable = typedArray.getDrawable(0)
            typedArray.recycle()
            musicShuffleButton.foreground = drawable
        }

        // Status Bar
        // VERSION >= KITKAT keep visibility else alpha 0
        untilAPI(Build.VERSION_CODES.KITKAT) {
            statusBar.alpha = 0F
        }
    }

    fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.mainPageToolbar)

        toolbar.navigationIcon = if (isAPIBelow(Build.VERSION_CODES.LOLLIPOP)) {
                                        resources.getDrawable(R.drawable.vector_material_hambuger)
                                 } else {
                                        getDrawable(R.drawable.vector_material_hambuger)
                                 }

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun setupBottomSlideLayout() {
        val bottomSheet: View = findViewById(R.id.mainPageMusicPlayer)
        this.musicPlayerBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        musicPlayerBottomSheetBehavior?.peekHeight = resources.getDimension(R.dimen.musicplayersheet_peek_height).toInt()
        musicPlayerBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        // Setting up Slide Fragments and stuff
        this.musicPlayerBottomSheetCallback = MusicPlayerBottomSheetCallback()
        musicPlayerBottomSheetCallback?.addEventListener(this.musicPlayerFragment)
        musicPlayerBottomSheetBehavior?.setBottomSheetCallback(this.musicPlayerBottomSheetCallback)
    }

    fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addPage("Songs", MainPageSongList())
        adapter.addPage("Albums", MainPageAlbumList())
        adapter.addPage("Artists", MainPageArtistList())
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        if (musicPlayerFragment.slideState == BottomSheetBehavior.STATE_EXPANDED) {
            musicPlayerBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}
