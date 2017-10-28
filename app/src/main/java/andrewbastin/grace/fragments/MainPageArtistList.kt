package andrewbastin.grace.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrewbastin.grace.R
import andrewbastin.grace.adapters.ArtistRecyclerViewAdapter
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.singletons.GraceHttpClient
import andrewbastin.grace.utils.ColorUtils
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.afollestad.appthemeengine.ATE
import com.afollestad.appthemeengine.Config
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [MainPageArtistList.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPageArtistList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_page_artist_list, container, false)

        val recyclerView: FastScrollRecyclerView = view.findViewById(R.id.mainPageArtistListRecyclerView)
        val adapter = ArtistRecyclerViewAdapter(context, GraceHttpClient.client, MusicCollection.artists)
        adapter.setHasStableIds(true)
        val layoutManager = LinearLayoutManager(context)

        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        recyclerView.itemAnimator = DefaultItemAnimator()

        // Theming
        if (ATE.config(activity, null).isConfigured) {

            val accentColor = Config.accentColor(activity, null)

            recyclerView.setPopupBgColor(accentColor)
            recyclerView.setThumbColor(accentColor)
            recyclerView.setPopupTextColor(ColorUtils.getBlackWhiteContrast(accentColor))

        }

        return view
    }

}
