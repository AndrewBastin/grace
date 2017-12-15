package andrewbastin.grace.fragments


import andrewbastin.grace.R
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.helpers.LastfmAPIHelper
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.untilAPI
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import co.metalab.asyncawait.async


/**
 * A simple [Fragment] subclass.
 */
class ArtistDetailArtistAboutFragment : Fragment() {

    val artist: Artist by lazy {
        MusicCollection.getArtistById(arguments.getLong(ID_BUNDLE_ARTIST_ID, -1L))!!
    }

    val bioTextView: TextView by bind(R.id.artistDetailArtistInfoBioText)
    val bioLayout: RelativeLayout by bind(R.id.artistDetailArtistInfoBioLayout)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_artist_detail_artist_about, container, false)
    }

    @SuppressLint("NewApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        async {
           bioLayout.visibility = View.GONE
            try {
                val bio = await { LastfmAPIHelper.getArtistBio(artist) }
                aboveAPI(24, true) {
                    bioTextView.text = Html.fromHtml(bio, Html.FROM_HTML_MODE_COMPACT)
                }
                untilAPI(24) {
                    bioTextView.text = Html.fromHtml(bio)
                }
                bioLayout.visibility = View.VISIBLE
            } catch (e: Exception) {

            }
        }
    }

    companion object {

        val ID_BUNDLE_ARTIST_ID = "andrewbastin.grace.artistdetailartistaboutfragment.artist.id"

        fun createInstance(artistID: Long): ArtistDetailArtistAboutFragment {
            val bundle = Bundle()
            bundle.putLong(ID_BUNDLE_ARTIST_ID, artistID)
            val fragment = ArtistDetailArtistAboutFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

}
