package andrewbastin.grace.services

import andrewbastin.grace.R
import andrewbastin.grace.extensions.accessSafely
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.singletons.GraceNotificationChannels
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.app.NotificationCompat
import android.support.v7.graphics.Palette
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import com.google.android.exoplayer2.*

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.lang.ref.WeakReference

class MusicPlayerService : Service() {

    companion object {
        val ACTION_PLAY = "andrewbastin.grace.actions.musicplayerservice.play"
        val ACTION_PAUSE = "andrewbastin.grace.actions.musicplayerservice.pause"
        val ACTION_NEXT = "andrewbastin.grace.actions.musicplayerservice.next"
        val ACTION_PREVIOUS = "andrewbastin.grace.actions.musicplayerservice.previous"
        val ACTION_DELETE = "andrewbastin.grace.actions.musicplayerservice.delete"

        val CHANGE_STYLE_GOTO_NEXT = 1
        val CHANGE_STYLE_GOTO_PREVIOUS = 2
        val CHANGE_STYLE_OTHER = 3

        val SERVICE_ID = 100
        val NOTIF_ID = 10

        val REQUEST_PAUSE = 1001
        val REQUEST_PLAY = 1002
        val REQUEST_PREV = 1003
        val REQUEST_NEXT = 1004
        val REQUEST_DEL = 1005
    }

    val binder = MusicPlayerServiceBinder()

    var playQueue = Queue(songs = arrayOf())
        private set

    // region Noisy Broadcast Receiver

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val noisyBroadcastReceiver = object:BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    val messageToService = Intent(context, MusicPlayerService::class.java)
                    messageToService.action = MusicPlayerService.ACTION_PAUSE

                    startService(messageToService)
                }
            }
        }

    }

    // endregion

    // region ExoPlayer objects

    private val player: SimpleExoPlayer by lazy(LazyThreadSafetyMode.NONE) {
        val value = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        value.addListener(playerEventListener)
        value
    }
    private val dataSourceFactory by lazy(LazyThreadSafetyMode.NONE) {
        DefaultDataSourceFactory(this, "grace-app")
    }
    private val extractorsFactory = DefaultExtractorsFactory()

    private val playerEventListener = object: ExoPlayer.EventListener {
        override fun onRepeatModeChanged(repeatMode: Int) {

        }

        /**
         * Called when the current playback parameters change. The playback parameters may change due to
         * a call to [ExoPlayer.setPlaybackParameters], or the player itself
         * may change them (for example, if audio playback switches to passthrough mode, where speed
         * adjustment is no longer possible).

         * @param playbackParameters The playback parameters.
         */
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        /**
         * Called when the available or selected tracks change.

         * @param trackGroups The available tracks. Never null, but may be of length zero.
         * *
         * @param trackSelections The track selections for each [Renderer]. Never null and always
         * *     of length [.getRendererCount], but may contain null elements.
         */
        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        }

        /**
         * Called when an error occurs. The playback state will transition to [.STATE_IDLE]
         * immediately after this method is called. The player instance can still be used, and
         * [.release] must still be called on the player should it no longer be required.

         * @param error The error.
         */
        override fun onPlayerError(error: ExoPlaybackException?) {
        }

        /**
         * Called when the value returned from either [.getPlayWhenReady] or
         * [.getPlaybackState] changes.

         * @param playWhenReady Whether playback will proceed when ready.
         * *
         * @param playbackState One of the `STATE` constants defined in the [ExoPlayer]
         * *     interface.
         */
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                playNext()
            }
        }

        /**
         * Called when the player starts or stops loading the source.

         * @param isLoading Whether the source is currently being loaded.
         */
        override fun onLoadingChanged(isLoading: Boolean) {
        }

        /**
         * Called when a position discontinuity occurs without a change to the timeline. A position
         * discontinuity occurs when the current window or period index changes (as a result of playback
         * transitioning from one period in the timeline to the next), or when the playback position
         * jumps within the period currently being played (as a result of a seek being performed, or
         * when the source introduces a discontinuity internally).
         *
         *
         * When a position discontinuity occurs as a result of a change to the timeline this method is
         * *not* called. [.onTimelineChanged] is called in this case.
         */
        override fun onPositionDiscontinuity() {
        }

        /**
         * Called when the timeline and/or manifest has been refreshed.
         *
         *
         * Note that if the timeline has changed then a position discontinuity may also have occurred.
         * For example, the current period index may have changed as a result of periods being added or
         * removed from the timeline. This will *not* be reported via a separate call to
         * [.onPositionDiscontinuity].

         * @param timeline The latest timeline. Never null, but may be empty.
         * *
         * @param manifest The latest manifest. May be null.
         */
        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        }

    }

    // endregion

    // region MediaSession objects

    private val playbackStateBuilder = PlaybackStateCompat.Builder()
    private val mediaMetadataBuilder = MediaMetadataCompat.Builder()
    private var mediaMetadata: MediaMetadataCompat = mediaMetadataBuilder.build()
    private var playbackState: PlaybackStateCompat = playbackStateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1F).build()
    private val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(this, "Grace")
    }

    // endregion

    // region Audio Manager objects

    private val audioManager: AudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private var isAudioDucking = false

    private val audioFocusChangeListener = object:AudioManager.OnAudioFocusChangeListener {

        override fun onAudioFocusChange(newState: Int) {

            when (newState) {

                AudioManager.AUDIOFOCUS_LOSS -> pause()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (playbackState.state != PlaybackStateCompat.STATE_PLAYING) play(true)
                    if (isAudioDucking) {
                        isAudioDucking = false
                        player.volume = 1F
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    player.volume = 0.5F
                    isAudioDucking = true
                }

            }

        }

    }

    // endregion

    // region Listener Objects

    private val eventListeners: MutableList<WeakReference<Listener>> = mutableListOf()

    // endregion



    // region Service Functions

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createMediaSession()

        when (intent?.action) {

            ACTION_PLAY -> play(true)
            ACTION_PAUSE -> pause()
            ACTION_PREVIOUS -> playPrevious()
            ACTION_NEXT -> playNext()
            ACTION_DELETE -> stopSelf()

        }

        return Service.START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        unregisterReceiver(noisyBroadcastReceiver)
        player.release()
        mediaSession.release()

    }

    // endregion

    // region Audio Playback Functions

    /**
     * @param resuming Whether the play is just resuming from a pause
     */
    fun play(resuming: Boolean = false) {
        val audioReqResult = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        if (audioReqResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (resuming) {
                player.playWhenReady = true
            } else {
                player.prepare(getMediaSourceForSong(playQueue.currentSong!!), true, true)
                player.playWhenReady = true
            }

            registerReceiver(noisyBroadcastReceiver, noisyIntentFilter)
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updateMediaSessionMetadata()
        }

        startForeground(NOTIF_ID, buildNotification())
    }

    fun pause() {
        player.playWhenReady = false
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED)
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        unregisterReceiver(noisyBroadcastReceiver)
        stopForeground(false)
        NotificationManagerCompat.from(this).notify(NOTIF_ID, buildNotification())
    }

    fun playNext() {
        setPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
        playQueue.goToNext()
        play()
        callOnSongChanged(CHANGE_STYLE_GOTO_NEXT)
    }

    fun playPrevious() {
        if (player.currentPosition > 3000)
            seekTo(0)
        else {
            setPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
            playQueue.goToPrevious()
            play()
            callOnSongChanged(CHANGE_STYLE_GOTO_PREVIOUS)
        }
    }

    fun seekTo(positionMS: Long) {
        player.seekTo(positionMS)
    }

    // endregion

    // region Audio Playback Info Functions

    fun getCurrentPlaybackPosition() = player.currentPosition
    fun getCurrentPlaybackDuration() = player.duration

    // endregion

    // region ExoPlayer Helper Functions

    private fun getMediaSourceForSong(song: Song): MediaSource {
        return ExtractorMediaSource(song.getFileUri(), dataSourceFactory, extractorsFactory, null, null)
    }

    // endregion

    // region Play Queue Functions

    fun setPlayQueue(newQueue: Queue, startPlaying: Boolean = true) {
        playQueue = newQueue
        if (startPlaying) {
            play()
        }
        callOnQueueChanged()
    }

    // endregion

    // region MediaSession Functions

    private fun createMediaSession() {
        mediaSession.setCallback(

                object: MediaSessionCompat.Callback() {

                    override fun onPlay() {
                        play(true)
                    }

                    override fun onPause() {
                        pause()
                    }

                    override fun onSkipToNext() {
                        playNext()
                    }

                    override fun onSkipToPrevious() {
                        playPrevious()
                    }

                    override fun onStop() {
                        unregisterReceiver(noisyBroadcastReceiver)
                    }

                }

        )

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setPlaybackState(playbackState)

        mediaSession.setMetadata(mediaMetadata)
    }

    private fun updateMediaSessionMetadata() {
        mediaMetadata =
                mediaMetadataBuilder
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, playQueue.currentSong?.getAlbumArt(contentResolver))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playQueue.currentSong?.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, playQueue.currentSong?.albumName)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, playQueue.currentSong?.artistName)
                        .build()
        mediaSession.setMetadata(mediaMetadata)
    }

    private fun setPlaybackState(state: Int) {
        playbackState = playbackStateBuilder.setState(state, 0L, 1F).build()
        mediaSession.setPlaybackState(playbackState)
        callOnPlaybackStateChanged(state)
    }

    // endregion

    // region Notification Functions

    private fun buildNotification(): Notification {
        val previousIntent = Intent(this, MusicPlayerService::class.java)
        previousIntent.action = ACTION_PREVIOUS
        val prevPendingIntent = PendingIntent.getService(this, REQUEST_PREV, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, MusicPlayerService::class.java)
        nextIntent.action = ACTION_NEXT
        val nextPendingIntent = PendingIntent.getService(this, REQUEST_NEXT, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val deleteIntent = Intent(this, MusicPlayerService::class.java)
        deleteIntent.action = ACTION_DELETE
        val deletePendingIntent = PendingIntent.getService(this, REQUEST_DEL, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val spannableText = SpannableString("${playQueue.currentSong?.albumName} by ${playQueue.currentSong?.artistName}")
        spannableText.setSpan(StyleSpan(android.graphics.Typeface.BOLD), playQueue.currentSong?.albumName?.length!! + 1, playQueue.currentSong?.albumName?.length!! + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val albumArt = playQueue.currentSong?.getAlbumArt(contentResolver)

        val palette: Palette? = if (albumArt != null) Palette.from(albumArt).generate() else null



        return NotificationCompat.Builder(this, GraceNotificationChannels.ID_PLAYBACK)
                .setDeleteIntent(deletePendingIntent)
                .setOngoing(playbackState.state == PlaybackStateCompat.STATE_PLAYING)
                .addAction(R.drawable.ic_notif_prev, "Previous", prevPendingIntent)
                .addAction(addPlayOrPauseAction())
                .addAction(R.drawable.ic_notif_next, "Next", nextPendingIntent)
                .setStyle( android.support.v4.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2))
                .setSmallIcon(R.drawable.ic_music_player_notif)
                .setContentTitle(playQueue.currentSong?.title)
                .setContentText(spannableText)
                .setLargeIcon(albumArt)
                .setWhen(0L)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(palette?.getVibrantColor(Color.BLACK) ?: Color.BLACK)
                .build()
    }

    private fun addPlayOrPauseAction(): android.support.v4.app.NotificationCompat.Action {
        if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            val intent = Intent(this, MusicPlayerService::class.java)
            intent.action = ACTION_PAUSE
            val pendingIntent = PendingIntent.getService(this, REQUEST_PAUSE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return android.support.v4.app.NotificationCompat.Action(R.drawable.ic_notif_pause, "Pause", pendingIntent)
        } else {
            val intent = Intent(this, MusicPlayerService::class.java)
            intent.action = ACTION_PLAY
            val pendingIntent = PendingIntent.getService(this, REQUEST_PLAY, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return android.support.v4.app.NotificationCompat.Action(R.drawable.ic_notif_play, "Play", pendingIntent)
        }
    }

    // endregion

    // region Listener Functions

    fun addEventListener(eventListener: Listener) {
        eventListeners.add(WeakReference(eventListener))
    }

    private fun callOnSongChanged(changeStyle: Int) {
        eventListeners.forEach {
            it.accessSafely {
                onSongChanged(playQueue.currentSong!!, playQueue, changeStyle)
            }
        }
    }

    private fun callOnQueueChanged() {
        eventListeners.forEach {
            it.accessSafely {
                onQueueChanged(playQueue)
            }
        }
    }

    private fun callOnPlaybackStateChanged(playbackState: Int) {
        eventListeners.forEach {
            it.accessSafely {
                onPlaybackStateChanged(playbackState)
            }
        }
    }

    // endregion

    inner class MusicPlayerServiceBinder : Binder() {
        fun getService() = this@MusicPlayerService
    }

    interface Listener {

        fun onSongChanged(song: Song, playQueue: Queue, changeStyle: Int)
        fun onQueueChanged(newQueue: Queue)
        fun onPlaybackStateChanged(playbackState: Int)

    }

}
