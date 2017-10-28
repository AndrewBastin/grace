package andrewbastin.grace.music

import andrewbastin.grace.extensions.accessSafely
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.music.interfaces.MusicPlayerEventListener
import andrewbastin.grace.services.MusicPlayerService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import co.metalab.asyncawait.async
import java.lang.ref.WeakReference

object MusicPlayerManager : MusicPlayerService.Listener {

    private val eventListeners: MutableList<WeakReference<MusicPlayerEventListener>> = mutableListOf()

    val song
       get() = {
           musicPlayerService?.playQueue?.currentSong
       }
    var mediaPlayer: MediaPlayer? = null
    var musicPlayerService: MusicPlayerService? = null

    private var context: Context? = null

    fun setContext(context: Context) {
        this.context = context
    }

    fun addMusicPlayerListener(eventListener: MusicPlayerEventListener) {
        eventListeners.add(WeakReference(eventListener))
    }

    fun setCurrentSong(song: Song) {
        async {
            await {
                if (context != null) {
                    if (musicPlayerService != null) {
                        val musicQueue = Queue(Queue.TYPE_ALL_SONGS, MusicCollection.songs)
                        musicQueue.setIndexToSong(song)
                        musicPlayerService?.setPlayQueue(musicQueue, true)
                    }
                }
            }
            eventListeners.forEach {
                it.accessSafely {
                    onCurrentSongChange(song)
                }
            }
        }
    }

    fun playQueue(queue: Queue) {
        async {
            if (context != null) {
                if (musicPlayerService != null) {
                    await { musicPlayerService?.setPlayQueue(queue, true) }

                    eventListeners.forEach {
                        it.accessSafely {
                            onCurrentSongChange(queue.currentSong!!)
                        }
                    }
                }
            }
        }
    }

    fun startMusicPlayerService() {
        if (context != null) {
            context?.bindService(Intent(context, MusicPlayerService::class.java), object: ServiceConnection {
                override fun onServiceDisconnected(className: ComponentName?) {
                    musicPlayerService = null
                }

                override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
                    musicPlayerService = (binder as MusicPlayerService.MusicPlayerServiceBinder).getService()
                    musicPlayerService?.addEventListener(this@MusicPlayerManager)
                }
            }, Context.BIND_AUTO_CREATE)
            context?.startService(Intent(context, MusicPlayerService::class.java))
        }
    }

    /**
     * @param positionMS Position of the song in milliseconds
     */
    fun seekSongTo(positionMS: Long) {
        musicPlayerService?.seekTo(positionMS)
    }

    fun getCurrentSongDuration(): Long? {
        try {
            return musicPlayerService?.getCurrentPlaybackDuration()
        } catch (e: Exception) {
            return 0L
        }
    }

    fun getCurrentSongPosition(): Long? {
        try {
            return musicPlayerService?.getCurrentPlaybackPosition()
        } catch(e: Exception) {
            return 0L
        }
    }

    override fun onSongChanged(song: Song, playQueue: Queue, changeStyle: Int) {
        eventListeners.forEach {
            it.accessSafely {
                onCurrentSongChange(song)
            }
        }
    }

    override fun onQueueChanged(newQueue: Queue) {

    }

    override fun onPlaybackStateChanged(playbackState: Int) {

    }

}