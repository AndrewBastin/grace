package andrewbastin.grace.persistance.db.dbs

import andrewbastin.grace.persistance.db.daos.StoredPlaylistDAO
import andrewbastin.grace.persistance.db.entities.StoredPlaylist
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(entities = [(StoredPlaylist::class)], version = 1, exportSchema = false)
abstract class StoredPlaylistDatabase(): RoomDatabase() {

    abstract fun playlistDAO(): StoredPlaylistDAO

}