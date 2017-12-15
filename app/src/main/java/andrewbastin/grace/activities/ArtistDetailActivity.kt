package andrewbastin.grace.activities

import andrewbastin.grace.R
import andrewbastin.grace.adapters.ViewPagerAdapter
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.extensions.getImageAsBitmap
import andrewbastin.grace.fragments.ArtistDetailArtistAlbumsFragment
import andrewbastin.grace.fragments.ArtistDetailArtistSongsFragment
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.singletons.ArtistImageStore
import andrewbastin.grace.singletons.Prefs
import andrewbastin.grace.utils.ColorUtils
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.onAPI
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.ATEActivity

class ArtistDetailActivity : ATEActivity() {

    companion object {

        val INTENT_INFO_ARTIST_ID = "andrewbastin.grace.intents.artistdetailactivity.artistid"

    }

    val artist by lazy {

        val artistID = intent.getLongExtra(INTENT_INFO_ARTIST_ID, -1L)
        if (artistID != -1L) {

            val queryResult = MusicCollection.getArtistById(artistID)

            if (queryResult != null) {

                queryResult

            } else {
                finish()

                MusicCollection.PLACEHOLDER_ARTIST
            }

        } else {
            finish()

            MusicCollection.PLACEHOLDER_ARTIST
        }

    }

    private val artistImageView: ImageView by bind(R.id.artistDetailImageView)
    private val toolbar: Toolbar by bind(R.id.artistDetailToolbar)
    private val collapsingToolbar: CollapsingToolbarLayout by bind(R.id.artistDetailCollapsingToolbar)
    private val appBarLayout: AppBarLayout by bind(R.id.artistDetailAppBar)
    private val statusBar: View by bind(R.id.statusBar)
    private val tabLayout: TabLayout by bind(R.id.artistDetailTabLayout)
    private val viewPager: ViewPager by bind(R.id.artistDetailViewPager)


    override fun onCreate(savedInstanceState: Bundle?) {
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

        setContentView(R.layout.activity_artist_detail)

        setupToolbar()
        setUpImageView()
        setUpCollapsingToolbar()
        setupTabLayout()

        // Shared element transitions
        aboveAPI(Build.VERSION_CODES.LOLLIPOP, true) {
            artistImageView.transitionName = applicationContext.getString(R.string.transition_mainPageAlbumLstItemToAlbumDetail_Image)
            toolbar.transitionName = applicationContext.getString(R.string.transition_mainPageAlbumListItemToAlbumDetail_Text)
        }

        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.list_to_detail_activity_fade)
    }

    fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = artist.name
    }

    fun setUpImageView() {

        artistImageView.setImageBitmap(ArtistImageStore.getCachedImage(this, artist.name))

    }

    fun setUpCollapsingToolbar() {

        // Initializing the status bar with transparency
        statusBar.setBackgroundColor(Color.BLACK)
        statusBar.alpha = 0F

        var contrastColor = Color.WHITE

        val albumArt = artistImageView.getImageAsBitmap()

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

    fun setupTabLayout() {
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addPage("Albums", ArtistDetailArtistAlbumsFragment.createInstance(artist.id))
        pagerAdapter.addPage("Songs", ArtistDetailArtistSongsFragment.createInstance(artist.id))
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            android.R.id.home -> finish()

        }
        return super.onOptionsItemSelected(item)
    }


}
