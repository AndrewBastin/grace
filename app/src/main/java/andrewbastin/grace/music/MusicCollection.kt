package andrewbastin.grace.music

import andrewbastin.grace.helpers.MusicHelper
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.music.data.Playlist
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.singletons.GraceDBs
import android.content.ContentResolver
import co.metalab.asyncawait.async
import kotlin.properties.Delegates

/**
 * Manipulates and presents the user's music collection
 */
object MusicCollection {

    // region Placeholders

    // Placeholders are not meant to be displayed to the user

    val PLACEHOLDER_ARTIST = Artist(-1, "<placeholder>")

    // endregion


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

    var playlists: Array<Playlist> = arrayOf()

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
    fun loadMediaStoreData(contentResolver: ContentResolver) {
        songs = MusicHelper.getAllSongs(contentResolver)
        songs.sort()

        artists = MusicHelper.getAllArtists(songs)
        artists.sort()

        albums = MusicHelper.getAllAlbums(songs)
        albums.sort()

        initialized = true
    }

    fun loadPlaylists(postFunc: () -> Unit) {
        async {
            await { playlists = GraceDBs.playlistsDB.playlistDAO().getAllPlaylists().map { it.getPlaylist() }.toTypedArray() }

            postFunc()
        }
    }


    // region Queries

    fun getArtistById(id: Long) = artists.firstOrNull { it.id == id }

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

    fun getAlbumsFromArtist(artist: Artist): Array<Album> {

        val albums = arrayListOf<Album>()

        getSongsFromArtist(artist).forEach {

            if (!albums.contains(getAlbumById(it.albumID))) {
                albums.add(getAlbumById(it.albumID)!!)
            }

        }

        return albums.toTypedArray()
    }

    fun getAlbumById(id: Long) = albums.firstOrNull { it.id == id }

    fun getSongById(id: Long) = songs.firstOrNull { it.id == id }

    fun getSongsFromAlbum(album: Album) = songs.filter { it.albumID == album.id }.toTypedArray()

    fun getSongsFromArtist(artist: Artist) = songs.filter { it.artistID == artist.id }.toTypedArray()

    // endregion
}