package andrewbastin.grace.singletons

import andrewbastin.grace.persistance.db.dbs.StoredPlaylistDatabase
import android.arch.persistence.room.Room
import android.content.Context

object GraceDBs {

    val DB_PLAYLISTS = "db_playlists"

    lateinit var playlistsDB: StoredPlaylistDatabase
        private set

    fun loadDBs(context: Context) {

        if (this@GraceDBs::playlistsDB.isInitialized) playlistsDB = Room.databaseBuilder(context, StoredPlaylistDatabase::class.java, DB_PLAYLISTS).build()

    }

}