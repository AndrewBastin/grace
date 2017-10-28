package andrewbastin.grace.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import andrewbastin.grace.R
import andrewbastin.grace.adapters.AlbumRecyclerViewAdapter
import andrewbastin.grace.music.MusicCollection
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
 * Activities that contain this fragment must implement the
 * [MainPageAlbumList.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainPageAlbumList.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPageAlbumList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_page_album_list, container, false)
        val adapter = AlbumRecyclerViewAdapter(context, activity, MusicCollection.albums)
        val layoutManager = LinearLayoutManager(context)

        val recyclerView: FastScrollRecyclerView = view.findViewById(R.id.mainPageAlbumListRecyclerView)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        // Theming
        if (ATE.config(activity, null).isConfigured) {

            val accentColor = Config.accentColor(activity, null)

            recyclerView.setPopupBgColor(accentColor)
            recyclerView.setThumbColor(accentColor)
            recyclerView.setPopupTextColor(ColorUtils.getBlackWhiteContrast(accentColor))

        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
}
