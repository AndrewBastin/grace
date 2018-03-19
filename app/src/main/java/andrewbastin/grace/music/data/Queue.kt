package andrewbastin.grace.music.data

import andrewbastin.grace.extensions.accessSafely
import java.lang.ref.WeakReference

class Queue(val queueType: Int = Queue.TYPE_UNSPECIFIED, songs: Array<Song>, defaultIndex: Int = 0) {

    companion object {
        val TYPE_UNSPECIFIED = 0
        val TYPE_ALL_SONGS = 1
        val TYPE_ALBUM = 1
        val TYPE_ARTIST = 2
    }

    private var listeners: MutableList<WeakReference<Queue.UpdateListener>> = mutableListOf()

    val songQueue = songs.toMutableList()
    var currentIndex = defaultIndex
        private set

    var currentSong: Song? = null
        private set

    fun hasNext() = currentIndex != songQueue.size - 1

    fun hasPrev() = currentIndex != 0

    fun addListener(listener: UpdateListener) {
        listeners.add(WeakReference(listener))
    }

    fun goToNext(): Song? {
        if (hasNext()) {
            val newSong = songQueue[++currentIndex]
            currentSong = songQueue[currentIndex]

            listeners.forEach {
                it.accessSafely {
                    queueMovedToNextSong(newSong)
                }
            }

            return newSong
        } else {

            listeners.forEach {
                it.accessSafely {
                    queueCompleted()
                }
            }

            return null
        }
    }

    fun goToPrevious(): Song? {
        if (hasPrev()) {
            val newSong = songQueue[--currentIndex]
            currentSong = songQueue[currentIndex]

            listeners.forEach {
                it.accessSafely {
                    queueMovedToPreviousSong(newSong)
                }
            }

            return newSong
        } else {
            return null
        }
    }

    fun addToQueue(song: Song, index: Int = songQueue.size) {
        if (index <= currentIndex) {
            songQueue.add(index, song)
            currentIndex++
            currentSong = songQueue[currentIndex]
        } else {
            songQueue.add(index, song)
        }

        listeners.forEach {
            it.accessSafely {
                addedSongToQueue(song, index)
            }
        }
    }

    /**
     *  Sets index to the index of a song if the song exists, else, the index is not changed
     */
    fun setIndexToSong(song: Song) {
        songQueue.indices
                .filter { songQueue[it] == song }
                .forEach { goToIndex(it) }
    }

    fun goToIndex(index: Int) {
        currentIndex = index
        currentSong = songQueue[currentIndex]

        listeners.forEach {
            it.accessSafely {
                queueJumpedToIndex(currentIndex, songQueue[currentIndex])
            }
        }
    }

    fun goToBeginningOfQueue() {
        goToIndex(0)
    }

    fun goToEndOfQueue() {
        goToIndex(songQueue.size)
    }

    interface UpdateListener {

        fun queueMovedToNextSong(newSong: Song)
        fun queueMovedToPreviousSong(newSong: Song)
        fun queueJumpedToIndex(index: Int, song: Song)
        fun queueCompleted()
        fun addedSongToQueue(newSong: Song, index: Int)
    }
}