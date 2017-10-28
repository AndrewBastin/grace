package andrewbastin.grace.music.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat

class Song(val id: Long,
           val title: String,
           val filePath: String,
           val albumID: Long,
           val albumName: String,
           val albumArt: Uri,
           val artistID: Long,
           val artistName: String) : Comparable<Song> {

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Song) = title.compareTo(other.title)

    fun getAsMediaMetadata(contentResolver: ContentResolver): MediaMetadataCompat {
        val albumArt = getAlbumArt(contentResolver)

        return MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumName)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
                .build()
    }

    fun getAlbumArt(contentResolver: ContentResolver): Bitmap? {
        try {
            return MediaStore.Images.Media.getBitmap(contentResolver, albumArt)
        } catch (e: Exception) {
            return null
        }
    }

    fun getFileUri(): Uri = Uri.parse("file://$filePath")

}