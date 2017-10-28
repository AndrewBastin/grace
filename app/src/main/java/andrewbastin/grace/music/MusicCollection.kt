package andrewbastin.grace.music

import andrewbastin.grace.helpers.MusicHelper
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.music.data.Song
import android.content.ContentResolver

/**
 * Manipulates and presents the user's music collection
 */
object MusicCollection {

    /**
     * Array containing all detected songs in the user's device
     */
    var songs: Array<Song> = arrayOf()
        private set

    /**
     * Array containing all detected artists in the user's device
     */
    var artists: Array<Artist> = arrayOf()
        private set

    /**
     * Array containing all the detected albums in the user's device
     */
    var albums: Array<Album> = arrayOf()
        private set

    /**
     * Whether the Music Collection was initialized or not
     */
    var initialized = false
        private set

    /**
     * Initializes the music collection and fetches the music data of the user
     *
     * @param contentResolver Instance of ContentResolver to fetch the data using
     */
    fun initialize(contentResolver: ContentResolver) {
        songs = MusicHelper.getAllSongs(contentResolver)
        songs.sort()

        artists = MusicHelper.getAllArtists(songs)
        artists.sort()

        albums = MusicHelper.getAllAlbums(songs)
        albums.sort()

        initialized = true
    }

    fun getSongById(id: Long) = songs.firstOrNull { it.id == id }

    fun getArtistById(id: Long) = artists.firstOrNull { it.id == id }

    fun getAlbumById(id: Long) = albums.firstOrNull { it.id == id }

    fun getSongsFromAlbum(album: Album) = songs.filter { it.albumID == album.id }.toTypedArray()

    fun getArtistsInSongs(songs: Array<Song>): Array<Artist> {
        val filteredArtistIds = mutableSetOf<Long>()
        songs.forEach {
            filteredArtistIds.add(it.artistID)
        }
        val response = mutableListOf<Artist>()

        filteredArtistIds.forEach {
            response.add(getArtistById(it)!!)
        }

        return response.toTypedArray()
    }
}