package andrewbastin.grace.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrewbastin.grace.R
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.adapters.ArtistRecyclerViewAdapter
import andrewbastin.grace.helpers.LastfmAPIHelper
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.singletons.GraceHttpClient
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.untilAPI
import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.widget.RelativeLayout
import android.widget.TextView
import co.metalab.asyncawait.async


/**
 * A simple [Fragment] subclass.
 */
class AlbumDetailAlbumInfoFragment : Fragment() {

    val album: Album by lazy {
        MusicCollection.getAlbumById(arguments.getLong(ID_BUNDLE_ALBUM_ID, -1L))!!
    }
    val artistsFeat: Array<Artist> by lazy {
        MusicCollection.getArtistsInSongs(MusicCollection.getSongsFromAlbum(album))
    }
    val artistsFeatRecyclerView: RecyclerView by bind(R.id.albumDetailAlbumInfoArtistFeatRecyclerView)
    val bioTextView: TextView by bind(R.id.albumDetailAlbumInfoBioText)
    val bioLayout: RelativeLayout by bind(R.id.albumDetailAlbumInfoBioLayout)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater?.inflate(R.layout.fragment_album_detail_album_info, container, false)
    }

    @SuppressLint("NewApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val artistsFeatAdapter = ArtistRecyclerViewAdapter(context, GraceHttpClient.client, artistsFeat)
        val layoutManager = LinearLayoutManager(context)

        artistsFeatRecyclerView.setHasFixedSize(true)
        artistsFeatRecyclerView.adapter = artistsFeatAdapter
        artistsFeatRecyclerView.layoutManager = layoutManager

        async {
            bioLayout.visibility = View.GONE
            try {
                val bio = await { LastfmAPIHelper.getAlbumBio(album) }
                aboveAPI(24, true) {
                    bioTextView.text = Html.fromHtml(bio, Html.FROM_HTML_MODE_COMPACT)
                }
                untilAPI(24) {
                    bioTextView.text = Html.fromHtml(bio)
                }
                bioLayout.visibility = View.VISIBLE
            } catch(e: Exception) {

            }
        }
    }

    companion object {

        val ID_BUNDLE_ALBUM_ID = "andrewbastin.grace.albumdetailalbuminfofragment.album.id"

        fun createInstance(albumID: Long): AlbumDetailAlbumInfoFragment {
            val bundle = Bundle()
            bundle.putLong(ID_BUNDLE_ALBUM_ID, albumID)
            val fragment = AlbumDetailAlbumInfoFragment()
            fragment.arguments = bundle
            return fragment
        }

    }
}
