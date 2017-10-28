package andrewbastin.grace.fragments

import andrewbastin.grace.R
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.extensions.getImageAsBitmap
import andrewbastin.grace.interfaces.BottomSheetEventListener
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.music.interfaces.MusicPlayerEventListener
import andrewbastin.grace.utils.ColorUtils
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.untilAPI
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.minimize.com.seek_bar_compat.SeekBarCompat
import java.util.*
import kotlin.concurrent.timer

class MusicPlayerFragment : Fragment(), MusicPlayerEventListener, BottomSheetEventListener {

    var slideState = BottomSheetBehavior.STATE_COLLAPSED
        private set

    // Views
    private val albumArt: ImageView by bind(R.id.musicPlayerAlbumArt)
    private val songTitleText: TextView by bind(R.id.musicPlayerSongName)
    private val songArtistText: TextView by bind(R.id.musicPlayerArtistName)
    private val songAlbumText: TextView by bind(R.id.musicPlayerAlbumName)
    private val statusBar: View by bind(R.id.statusBar)
    private val seekBar: SeekBarCompat by bind(R.id.musicPlayerSeekBar)
    private val playPauseButton: ImageButton by bind(R.id.musicPlayerPlayPause)
    private val musicPlayerLayout: View by bind(R.id.musicPlayerLayout)
    private val miniPlayerLayout: RelativeLayout by bind(R.id.musicPlayerMiniPlayerLayout)
    private val miniPlayerSongTitle: TextView by bind(R.id.musicPlayerMiniPlayerSongText)
    private val miniPlayerSongInfo: TextView by bind(R.id.musicPlayerMiniPlayerSongInfoText)

    private var seekerUpdateTimer: Timer? = null
    private var isUserInteractingWithSeekBar = false
    private var lightStatusBarActive: Boolean = false

    override fun onCurrentSongChange(song: Song) {
        setAlbumArt(song.albumArt)
        updateColors()
        aboveAPI(Build.VERSION_CODES.JELLY_BEAN) {
            updateStatusBarLook()
        }

        songTitleText.text = song.title
        miniPlayerSongTitle.text = song.title

        aboveAPI(Build.VERSION_CODES.N, true) {
            songArtistText.text = Html.fromHtml("<b>by</b> <font color=\"#beffffff\">${song.artistName}</font>", Html.FROM_HTML_MODE_LEGACY)
            songAlbumText.text = Html.fromHtml("<b>from</b> <font color\"#beffffff\">${song.albumName}</font>", Html.FROM_HTML_MODE_LEGACY)
        }

        @Suppress("DEPRECATION")
        untilAPI(Build.VERSION_CODES.N) {
            songArtistText.text = Html.fromHtml("<b>by</b> <font color=\"#beffffff\">${song.artistName}</font>")
            songAlbumText.text = Html.fromHtml("<b>from</b> <font color\"#beffffff\">${song.albumName}</font>")
        }

        miniPlayerSongInfo.text = songArtistText.text

        if (seekerUpdateTimer == null) {
            seekerUpdateTimer = timer("Test", true, 0L, 5L) {
                try {
                    if (!isUserInteractingWithSeekBar) {
                        val currentSongPos = MusicPlayerManager.getCurrentSongPosition()!!.toInt()
                        aboveAPI(Build.VERSION_CODES.N, true) {
                            seekBar.setProgress(currentSongPos * 100 / MusicPlayerManager.getCurrentSongDuration()!!.toInt(), true)
                        }

                        untilAPI(Build.VERSION_CODES.N) {
                            seekBar.progress = currentSongPos * 100 / MusicPlayerManager.getCurrentSongDuration()!!.toInt()
                        }
                    }
                } catch(e: Exception) {
                }
            }
        }
    }

    override fun onSlide(slideOffset: Float) {
        miniPlayerLayout.alpha = 1 - slideOffset
    }

    override fun onSlideStateChanged(newState: Int) {
        slideState = newState
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            aboveAPI(Build.VERSION_CODES.LOLLIPOP) {
                view?.systemUiVisibility = 0
            }
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            aboveAPI(Build.VERSION_CODES.LOLLIPOP) {
                if (lightStatusBarActive) view?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        statusBar.setBackgroundColor(Color.BLACK)
        statusBar.alpha = 0F

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserInteractingWithSeekBar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserInteractingWithSeekBar = false
                if (seekBar != null) {
                    try {
                        MusicPlayerManager.seekSongTo((seekBar.progress * MusicPlayerManager.getCurrentSongDuration()!!) / 100)
                    } catch(e: Exception) {
                    }
                }
            }
        })

        playPauseButton.setOnClickListener {

            val animatable = playPauseButton.drawable as Animatable
            animatable.start()

        }
        super.onActivityCreated(savedInstanceState)
    }

    fun setAlbumArt(path: Uri) {
        try {
            albumArt.setImageURI(path)
        } catch (e: Exception) {

        }
    }

    fun updateColors() {
        try {
            Palette.from(albumArt.getImageAsBitmap()).generate {
                val oldBgColor = (musicPlayerLayout.background as ColorDrawable).color
                val bgAnimator = ValueAnimator.ofArgb(oldBgColor, it.getVibrantColor(Color.BLACK))

                val color = it.vibrantSwatch?.bodyTextColor ?: ColorUtils.getBlackWhiteContrast(it.getVibrantColor(Color.BLACK))

                val fgAnimator = ValueAnimator.ofArgb(songTitleText.currentTextColor, color)

                bgAnimator.duration = 500
                bgAnimator.addUpdateListener {
                    musicPlayerLayout.setBackgroundColor(bgAnimator.animatedValue as Int)
                }
                bgAnimator.start()

                fgAnimator.duration = 500
                fgAnimator.addUpdateListener {
                    songTitleText.setTextColor(fgAnimator.animatedValue as Int)
                    songArtistText.setTextColor(fgAnimator.animatedValue as Int)
                    songAlbumText.setTextColor(fgAnimator.animatedValue as Int)

                    // TODO : Add the other elements as well
                }

                fgAnimator.start()
            }
        } catch (e: Exception) {

        }
    }

    fun updateStatusBarLook() {
        try {
            val albumArtImage = albumArt.getImageAsBitmap()
            if (albumArtImage != null) {

                Palette.from(albumArtImage).generate {

                    val contrast = ColorUtils.getBlackWhiteContrast(it.getVibrantColor(Color.BLACK))
                    if (contrast == Color.BLACK) {
                        aboveAPI(Build.VERSION_CODES.JELLY_BEAN) {
                            statusBar.alpha = 0.25F
                        }
                        aboveAPI(Build.VERSION_CODES.LOLLIPOP) {
                            lightStatusBarActive = true
                            view?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }

                    } else {
                        aboveAPI(Build.VERSION_CODES.JELLY_BEAN) {
                            statusBar.alpha = 0F
                        }
                        aboveAPI(Build.VERSION_CODES.LOLLIPOP) {
                            lightStatusBarActive = false
                            view?.systemUiVisibility = 0
                        }
                    }
                }

            }

        } catch (e: Exception) {
        }
    }

}
