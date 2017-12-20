package andrewbastin.grace.music.data

import andrewbastin.grace.persistance.db.entities.StoredPlaylist
import andrewbastin.grace.singletons.GraceDBs
import co.metalab.asyncawait.async

class Playlist(val name: String, val songs: Array<Song>) {

    /**
     * ID of the playlist if it is stored already else null
     */
    var id: Long? = null
        private set

    constructor(storedPlaylist: StoredPlaylist): this(storedPlaylist.name, storedPlaylist.getSongsList()) {
        this.id = storedPlaylist.id
    }

    fun isPlaylistStored() = id != null

    /**
     * Inserts a new playlist to the database if not saved, else updates the playlist.
     */
    fun savePlaylist() {
        if (id == null) {
            async {
                id = GraceDBs.playlistsDB.playlistDAO().addNewPlaylistAndGetID(StoredPlaylist(this@Playlist))
            }
        } else {
            async {
                GraceDBs.playlistsDB.playlistDAO().updatePlaylist(StoredPlaylist(this@Playlist))
            }
        }
    }

    fun deletePlaylist() {
        if (id != null) {
            async {
                await { GraceDBs.playlistsDB.playlistDAO().deletePlaylist(StoredPlaylist(this@Playlist)) }
                id = null
            }
        }
    }
}
