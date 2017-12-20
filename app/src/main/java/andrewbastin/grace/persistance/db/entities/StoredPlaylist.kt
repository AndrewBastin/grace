package andrewbastin.grace.persistance.db.entities

import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.data.Playlist
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.persistance.db.converters.LongArrayTypeConverter
import android.arch.persistence.room.*
import java.util.*

@Entity(tableName = "playlist")
@TypeConverters(LongArrayTypeConverter::class)
class StoredPlaylist(
        @PrimaryKey(autoGenerate = true) var id: Long? = 0,
        var name: String = "",
        var songIDs: Array<Long> = arrayOf()
) {

    @Ignore constructor(playlist: Playlist): this(name = playlist.name, songIDs = playlist.songs.map { it.id }.toTypedArray()) {
        if (playlist.id != null) {
            id = playlist.id
        }
    }

    fun getSongsList(): Array<Song> {
        val result = mutableListOf<Song?>()

        songIDs.forEach {
            result.add(MusicCollection.getSongById(it))
        }

        return result.filterNotNull().toTypedArray()
    }

    fun getPlaylist() = Playlist(this)

}