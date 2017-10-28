package andrewbastin.grace.music.data

import android.net.Uri

class Album(val id: Long, val title: String, val artistID: Long, val albumArt: Uri, val artistName: String) : Comparable<Album> {

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Album) = title.compareTo(other.title)

    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?) = if (other is Album) other.id == this.id else false

}
