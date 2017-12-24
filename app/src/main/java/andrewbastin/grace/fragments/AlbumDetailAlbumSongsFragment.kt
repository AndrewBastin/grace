package andrewbastin.grace.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrewbastin.grace.R
import andrewbastin.grace.adapters.SongListRecylerViewAdapter
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.utils.ColorUtils
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.Config
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView


/**
 * A simple [Fragment] subclass.
 */
class AlbumDetailAlbumSongsFragment : Fragment() {

    val albumSongs: Array<Song> by lazy {
        getAlbumSongsFromArgs()
    }
    val songRecyclerView: FastScrollRecyclerView by bind(R.id.albumDetailAlbumSongFragmentSongRecyclerView)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_album_detail_album_songs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
    }


    fun setupRecyclerView() {
        val adapter = SongListRecylerViewAdapter(context, albumSongs) {
            val queue = Queue(Queue.TYPE_ALBUM, albumSongs)
            queue.setIndexToSong(it)
            MusicPlayerManager.playQueue(queue)
        }
        val layoutManager = LinearLayoutManager(context)

        songRecyclerView.setHasFixedSize(true)
        songRecyclerView.layoutManager = layoutManager
        songRecyclerView.adapter = adapter
        songRecyclerView.itemAnimator = DefaultItemAnimator()
        songRecyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        // Theming
        if (ATE.config(activity, null).isConfigured) {

            val accentColor = Config.accentColor(activity, null)

            songRecyclerView.setPopupBgColor(accentColor)
            songRecyclerView.setThumbColor(accentColor)
            songRecyclerView.setPopupTextColor(ColorUtils.getBlackWhiteContrast(accentColor))

        }
    }

    fun getAlbumSongsFromArgs(): Array<Song> {
        val albumID = arguments?.getLong(ID_BUNDLE_ALBUM_ID, -1)
        if (albumID != -1L) {
            return MusicCollection.getSongsFromAlbum(MusicCollection.getAlbumById(albumID!!)!!)

        } else activity.onBackPressed() // Something messed up :/
        return arrayOf()
    }

    companion object {

        val ID_BUNDLE_ALBUM_ID = "andrewbastin.grace.albumdetailalbumsongsfragment.id.bundle.albumid"

        fun createInstance(albumID: Long): AlbumDetailAlbumSongsFragment {
            val bundle = Bundle()
            bundle.putLong(ID_BUNDLE_ALBUM_ID, albumID)
            val fragment = AlbumDetailAlbumSongsFragment()
            fragment.arguments = bundle
            return fragment
        }

    }

}
