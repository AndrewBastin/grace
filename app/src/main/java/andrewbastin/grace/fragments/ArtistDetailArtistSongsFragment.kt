package andrewbastin.grace.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrewbastin.grace.R
import andrewbastin.grace.adapters.SongListRecylerViewAdapter
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.utils.ColorUtils
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.Config
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ArtistDetailArtistSongsFragment : Fragment() {

    companion object {
        val ID_BUNDLE_ARTIST_ID = "andrewbastin.grace.artistdetailartistsongsfragment.id.bundle.artistid"

        fun createInstance(artistID: Long): ArtistDetailArtistSongsFragment {
            val bundle = Bundle()
            bundle.putLong(ID_BUNDLE_ARTIST_ID, artistID)
            val fragment = ArtistDetailArtistSongsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    val artistSongs: Array<Song> by lazy {
        val artistID = arguments?.getLong(ID_BUNDLE_ARTIST_ID, -1)
        if (artistID != -1L) {

            MusicCollection.getSongsFromArtist(MusicCollection.getArtistById(artistID!!)!!)

        } else {

            activity.finish()
            arrayOf()
        }
    }

    private val songRecyclerView: FastScrollRecyclerView by bind(R.id.artistDetailArtistSongFragmentSongRecyclerView)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_artist_detail_artist_songs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
    }

    fun setupRecyclerView() {
        val listAdapter = SongListRecylerViewAdapter(context, artistSongs) {
            val queue = Queue(Queue.TYPE_ARTIST, artistSongs)
            queue.setIndexToSong(it)
            MusicPlayerManager.playQueue(queue)
        }

        val listLayoutManager = LinearLayoutManager(context)

        songRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = listLayoutManager
            adapter = listAdapter
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, listLayoutManager.orientation))
        }

        // Theming
        if (ATE.config(activity, null).isConfigured) {

            val accentColor = Config.accentColor(activity, null)

            songRecyclerView.setPopupBgColor(accentColor)
            songRecyclerView.setThumbColor(accentColor)
            songRecyclerView.setPopupTextColor(ColorUtils.getBlackWhiteContrast(accentColor))

        }
    }

}