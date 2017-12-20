package andrewbastin.grace.persistance.db.daos

import andrewbastin.grace.persistance.db.entities.StoredPlaylist
import android.arch.persistence.room.*

@Dao interface StoredPlaylistDAO {

    @Query("select * from playlist")
    fun getAllPlaylists(): List<StoredPlaylist>

    @Query("select * from where id = :p0")
    fun getPlaylistWithID(): StoredPlaylist

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewPlaylist(storedPlaylist: StoredPlaylist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewPlaylistAndGetID(storedPlaylist: StoredPlaylist): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlaylist(storedPlaylist: StoredPlaylist)

    @Delete
    fun deletePlaylist(storedPlaylist: StoredPlaylist)

}