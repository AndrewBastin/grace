package andrewbastin.grace.adapters

import andrewbastin.grace.R
import andrewbastin.grace.activities.AlbumDetailActivity
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.utils.aboveAPI
import andrewbastin.grace.utils.untilAPI
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.squareup.picasso.Picasso

class AlbumRecyclerViewAdapter(val context: Context, val ownerActivity: Activity, val albums: Array<Album>) : RecyclerView.Adapter<AlbumRecyclerViewViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    override fun getSectionName(position: Int) = albums[position].title.substring(0, 1)

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.

     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * *               an adapter position.
     * *
     * @param viewType The view type of the new View.
     * *
     * *
     * @return A new ViewHolder that holds a View of the given view type.
     * *
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumRecyclerViewViewHolder? {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_albumlist, parent, false)
        return AlbumRecyclerViewViewHolder(itemView)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.

     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = albums.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.

     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.

     * @param holder The ViewHolder which should be updated to represent the contents of the
     * *        item at the given position in the data set.
     * *
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: AlbumRecyclerViewViewHolder?, position: Int) {
        holder?.titleTextView?.text = albums[position].title
        holder?.artistTextView?.text = albums[position].artistName
        holder?.albumID = albums[position].id
        holder?.ownerActivity = ownerActivity

        Picasso.with(context).load(albums[position].albumArt).into(holder?.albumArtImageView)
    }

}

class AlbumRecyclerViewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val titleTextView: TextView = view.findViewById(R.id.mainPageAlbumListTitle)
    val artistTextView: TextView = view.findViewById(R.id.mainPageAlbumListArtist)
    val albumArtImageView: ImageView = view.findViewById(R.id.mainPageAlbumListImage)

    var albumID: Long? = null
    var ownerActivity: Activity? = null

    init {
        view.setOnClickListener {

            if (albumID != null) {

                // Shared Element transitions
                aboveAPI(Build.VERSION_CODES.LOLLIPOP, true) {
                    val intent = Intent(view.context, AlbumDetailActivity::class.java)
                    intent.putExtra(AlbumDetailActivity.INTENT_INFO_ALBUM_ID, albumID!!)

                    val imagePair: android.support.v4.util.Pair<View, String> = android.support.v4.util.Pair(albumArtImageView, albumArtImageView.transitionName)

                    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ownerActivity, imagePair)

                    view.context.startActivity(intent, activityOptions.toBundle())
                }

                untilAPI(Build.VERSION_CODES.LOLLIPOP) {
                    val intent = Intent(view.context, AlbumDetailActivity::class.java)
                    intent.putExtra(AlbumDetailActivity.INTENT_INFO_ALBUM_ID, albumID!!)
                    view.context.startActivity(intent)
                }

            }

        }

        // Setting up for Shared Element Transitions
        aboveAPI(Build.VERSION_CODES.LOLLIPOP, true) {
            albumArtImageView.transitionName = view.context.getString(R.string.transition_mainPageAlbumLstItemToAlbumDetail_Image)
            titleTextView.transitionName = view.context.getString(R.string.transition_mainPageAlbumListItemToAlbumDetail_Text)
        }
    }
}