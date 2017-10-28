package andrewbastin.grace.activities

import android.os.Bundle
import android.support.v7.widget.Toolbar
import andrewbastin.grace.R
import andrewbastin.grace.adapters.ViewPagerAdapter
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.extensions.getImageAsBitmap
import andrewbastin.grace.fragments.AlbumDetailAlbumInfoFragment
import andrewbastin.grace.fragments.AlbumDetailAlbumSongsFragment
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.singletons.Prefs
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.graphics.Palette
import android.widget.ImageView
import andrewbastin.grace.utils.ColorUtils
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.onAPI
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.ATEActivity

class AlbumDetailActivity : ATEActivity() {

    companion object {

        val INTENT_INFO_ALBUM_ID = "andrewbastin.grace.intents.albumdetailactivity.albumid"

    }

    var album: Album? = null

    val albumArtImageView: ImageView by bind(R.id.albumDetailAlbumArt)
    val toolbar: Toolbar by bind(R.id.albumDetailToolbar)
    val collapsingToolbar: CollapsingToolbarLayout by bind(R.id.albumDetailCollapsingToolbar)
    val appBarLayout: AppBarLayout by bind(R.id.albumDetailAppBar)
    val statusBar: View by bind(R.id.statusBar)
    val tabLayout: TabLayout by bind(R.id.albumDetailTabLayout)
    val viewPager: ViewPager by bind(R.id.albumDetailViewPager)

    val songListFragment: AlbumDetailAlbumSongsFragment by lazy {
        AlbumDetailAlbumSongsFragment.createInstance(album?.id!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!ATE.config(this, null).isConfigured) {
            ATE.config(this, null).apply {

                activityTheme( if (Prefs.ThemePref.darkMode) R.style.AppTheme_Dark else R.style.AppTheme )
                toolbarColor(Prefs.ThemePref.primaryColor)
                primaryColor(Prefs.ThemePref.primaryColor)
                accentColor(Prefs.ThemePref.accentColor)
                statusBarColor(Color.TRANSPARENT)

            }.commit()
        }

        super.onCreate(savedInstanceState)

        getAlbumData()
        setupToolbar()
        setUpImageView()
        setUpCollapsingToolbar()
        setupTabLayout()

        // Shared element transitions
        aboveAPI(Build.VERSION_CODES.LOLLIPOP, true) {
            albumArtImageView.transitionName = applicationContext.getString(R.string.transition_mainPageAlbumLstItemToAlbumDetail_Image)
            toolbar.transitionName = applicationContext.getString(R.string.transition_mainPageAlbumListItemToAlbumDetail_Text)
        }

        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.list_to_detail_activity_fade)
    }

    fun getAlbumData() {
        // Getting album
        val albumID = intent.getLongExtra(INTENT_INFO_ALBUM_ID, -1)
        if (albumID != -1L) {
            album = MusicCollection.getAlbumById(albumID)
        } else {
            onBackPressed() // Exit out of the activity... Something's messed up :/
        }
    }

    fun setupToolbar() {
        setContentView(R.layout.activity_album_detail)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = album?.title
    }

    fun setUpImageView() {
        albumArtImageView.setImageURI(album?.albumArt)
    }

    fun setUpCollapsingToolbar() {

        // Initializing the status bar with transparency
        statusBar.setBackgroundColor(Color.BLACK)
        statusBar.alpha = 0F

        var contrastColor = Color.WHITE

        val albumArt = albumArtImageView?.getImageAsBitmap()

        if (albumArt != null) {
            Palette.from(albumArt).generate {
                collapsingToolbar.setContentScrimColor(it.getVibrantColor(Color.BLACK))
                collapsingToolbar.statusBarScrim = ColorDrawable(ColorUtils.darken(it.getVibrantColor(Color.BLACK), 0.8F))
                collapsingToolbar.setCollapsedTitleTextColor(ColorUtils.getBlackWhiteContrast(it.getVibrantColor(Color.BLACK)))
                collapsingToolbar.setExpandedTitleColor(ColorUtils.getBlackWhiteContrast(it.getVibrantColor(Color.BLACK)))

                tabLayout.setBackgroundColor(it.getVibrantColor(Color.BLACK))

                // On Lollipop set status Bar Scrim to the same color as statusBar will cover that up...
                onAPI(Build.VERSION_CODES.LOLLIPOP) {
                    collapsingToolbar.statusBarScrim = ColorDrawable(it.getVibrantColor(Color.BLACK))
                }

                contrastColor = ColorUtils.getBlackWhiteContrast(it.getDominantColor(Color.BLACK))
            }
            collapsingToolbar.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT))
            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                run {
                    if ((collapsingToolbar.height + verticalOffset) < (2 * ViewCompat.getMinimumHeight(collapsingToolbar))) {
                        toolbar.navigationIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                        toolbar.overflowIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

                        // Resetting Dark status bar for Marshmallow
                        aboveAPI(Build.VERSION_CODES.M, true) {
                            appBarLayout.systemUiVisibility = 0
                        }

                    } else {
                        toolbar.navigationIcon?.setColorFilter(contrastColor, PorterDuff.Mode.SRC_ATOP)
                        toolbar.overflowIcon?.setColorFilter(contrastColor, PorterDuff.Mode.SRC_ATOP)

                        // Dark Status Icons for Marshmallow and above
                        aboveAPI(Build.VERSION_CODES.M, true) {
                            if (contrastColor == Color.BLACK) {
                                appBarLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                        }

                        // Dark Status Bar Background for lollipop and above
                        onAPI(Build.VERSION_CODES.LOLLIPOP) {
                            if (contrastColor == Color.BLACK) {
                                statusBar.setBackgroundColor(Color.BLACK)
                                statusBar.alpha = 0.2F
                            }
                        }
                    }
                }
            }
        } else {
            collapsingToolbar.setContentScrimColor(Color.BLACK)
            collapsingToolbar.statusBarScrim = ColorDrawable(Color.BLACK)
            tabLayout.setBackgroundColor(Color.BLACK)
        }
    }

    override fun onBackPressed() {
        appBarLayout.systemUiVisibility = 0 // Resetting the dark status bar effect (if applied) on Marshmallow
        statusBar.alpha = 0F // Resetting Status Bar effect applied on Lollipop
        super.onBackPressed()
    }

    fun setupTabLayout() {
        // Setting up the View Pager
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addPage("About", AlbumDetailAlbumInfoFragment.createInstance(album?.id!!))
        pagerAdapter.addPage("Songs", songListFragment)
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_album_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.albumDetailMenu_Play -> {
                MusicPlayerManager.playQueue(Queue(Queue.TYPE_ALBUM, songListFragment.albumSongs))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
