package andrewbastin.grace.helpers

import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.music.data.Song
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

object MusicHelper {

    val albumArtContentUri = Uri.parse("content://media/external/audio/albumart")

    fun getAllSongs(contentResolver: ContentResolver) : Array<Song> {

        val response: MutableList<Song> = mutableListOf()

        val projection = listOf(
                MediaStore.Audio.Media._ID,         // 0
                MediaStore.Audio.Media.TITLE,       // 1
                MediaStore.Audio.Media.DATA,        // 2
                MediaStore.Audio.Media.ALBUM_ID,    // 3
                MediaStore.Audio.Media.ALBUM,       // 4
                MediaStore.Audio.Media.ARTIST_ID,   // 5
                MediaStore.Audio.Media.ARTIST       // 6
        )
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection.toTypedArray(), "${MediaStore.Audio.Media.IS_MUSIC} != 0", null, null)

        while (cursor.moveToNext()) {
            response.add(
                    Song(
                            id = cursor.getLong(0),
                            title = cursor.getString(1),
                            filePath = cursor.getString(2),
                            albumID = cursor.getLong(3),
                            albumName = tidyUpUnknownAlbums(cursor.getString(4)),
                            albumArt = ContentUris.withAppendedId(albumArtContentUri, cursor.getLong(3)),
                            artistID = cursor.getLong(5),
                            artistName = tidyUpUnknownArtists(cursor.getString(6))
                    ))
        }

        cursor.close()

        response.sort()

        return response.toTypedArray()
    }

    fun getAllArtists(songs: Array<Song>): Array<Artist> {
        val response = mutableListOf<Artist>()

        for (song in songs) {
            if (!response.any { it.id == song.artistID }) {
                response.add(Artist(song.artistID, song.artistName))
            }
        }

        response.sort()

        return response.toTypedArray()
    }

    fun getAllAlbums(songs: Array<Song>): Array<Album> {
        val response = mutableListOf<Album>()

        for (song in songs) {
            if (!response.any { it.id == song.albumID }) {
                response.add(Album(song.albumID, song.albumName, song.artistID, ContentUris.withAppendedId(albumArtContentUri, song.albumID), song.artistName))
            }
        }
        response.sort()

        return response.toTypedArray()
    }

    private fun tidyUpUnknownArtists(artistName: String) = if (artistName != "<unknown>") artistName else "Unknown Artist"
    private fun tidyUpUnknownAlbums(albumName: String) = if (albumName != "<unknown>") albumName else "Unknown Album"
}