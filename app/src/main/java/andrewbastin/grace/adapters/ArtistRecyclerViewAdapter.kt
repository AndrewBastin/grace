package andrewbastin.grace.adapters

import andrewbastin.grace.R
import andrewbastin.grace.activities.ArtistDetailActivity
import andrewbastin.grace.music.MusicCollection
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.singletons.ArtistImageStore
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import co.metalab.asyncawait.async
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import okhttp3.OkHttpClient

class ArtistRecyclerViewAdapter(val context: Context, val httpClient: OkHttpClient, val artists: Array<Artist>) : RecyclerView.Adapter<ArtistRecyclerViewViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    override fun getSectionName(position: Int) = artists[position].name.substring(0, 1)

    /**
     * Returns the total number of items in the data set held by the adapter.

     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = artists.size

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
    override fun onBindViewHolder(holder: ArtistRecyclerViewViewHolder?, position: Int) {
        holder?.artistTitleText?.text = artists[position].name
        holder?.artistImageView?.setImageDrawable(null)
        holder?.artistID = artists[position].id

        async {
            ArtistImageStore.fetchImage(context, artists[position].name) { image, requestedArtistName ->
                if (requestedArtistName == artists[position].name) {

                    if (image != null) {
                        holder?.artistImageView?.setImageBitmap(image)
                        holder?.artistImageView?.requestLayout()
                    }

                }
            }
        }
    }

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
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArtistRecyclerViewViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_artistlist, parent, false)

        return ArtistRecyclerViewViewHolder(itemView)
    }

    override fun getItemId(position: Int) = artists[position].id
}

class ArtistRecyclerViewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener {

            if (artistID != MusicCollection.PLACEHOLDER_ARTIST.id) {
                val intent = Intent(view.context, ArtistDetailActivity::class.java)
                intent.putExtra(ArtistDetailActivity.INTENT_INFO_ARTIST_ID, artistID)

                view.context.startActivity(intent)
            }
        }
    }

    val artistTitleText: TextView = view.findViewById(R.id.mainPageArtistListTitle)
    val artistImageView: ImageView = view.findViewById(R.id.mainPageArtistListImage)

    var artistID: Long = MusicCollection.PLACEHOLDER_ARTIST.id
}