package andrewbastin.grace.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrewbastin.grace.R
import andrewbastin.grace.adapters.AlbumRecyclerViewAdapter
import andrewbastin.grace.adapters.SongListRecylerViewAdapter
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.MusicPlayerManager
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Queue
import andrewbastin.grace.utils.ColorUtils
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.Config
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ArtistDetailArtistAlbumsFragment : Fragment() {

    companion object {
        val ID_BUNDLE_ARTIST_ID = "andrewbastin.grace.artistdetailartistalbumsfragment.id.bundle.artistid"

        fun createInstance(artistID: Long): ArtistDetailArtistAlbumsFragment {
            val bundle = Bundle()
            bundle.putLong(ID_BUNDLE_ARTIST_ID, artistID)
            val fragment = ArtistDetailArtistAlbumsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    val artistAlbums: Array<Album> by lazy {
        val artistID = arguments?.getLong(ArtistDetailArtistAlbumsFragment.ID_BUNDLE_ARTIST_ID, -1)
        if (artistID != -1L) {

            MusicCollection.getAlbumsFromArtist(MusicCollection.getArtistById(artistID!!)!!)

        } else {
            activity.finish()

            arrayOf()
        }
    }

    val recyclerView: FastScrollRecyclerView by bind(R.id.artistDetailArtistAlbumsFragmentRecyclerView)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_artist_detail_artist_albums, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
    }

    fun setupRecyclerView() {
        val listAdapter = AlbumRecyclerViewAdapter(context, activity, artistAlbums)
        val listLayoutManager = LinearLayoutManager(context)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = listLayoutManager
            adapter = listAdapter
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, listLayoutManager.orientation))
        }

        // Theming
        if (ATE.config(activity, null).isConfigured) {

            val accentColor = Config.accentColor(activity, null)

            recyclerView.setPopupBgColor(accentColor)
            recyclerView.setThumbColor(accentColor)
            recyclerView.setPopupTextColor(ColorUtils.getBlackWhiteContrast(accentColor))

        }
    }

}
