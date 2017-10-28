package andrewbastin.grace.music.interfaces

import andrewbastin.grace.music.data.Song

interface MusicPlayerEventListener {

    fun onCurrentSongChange(song: Song)

}