package andrewbastin.grace.adapters

import andrewbastin.grace.R
import andrewbastin.grace.music.data.Song
import andrewbastin.grace.music.MusicPlayerManager
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.squareup.picasso.Picasso

class SongListRecylerViewAdapter(val context: Context, val songs: Array<Song>, val onItemClickFunc: (song: Song) -> Unit) : RecyclerView.Adapter<SongListRecyclerViewViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    override fun getSectionName(position: Int) = songs[position].title.substring(0,1)

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
    override fun onBindViewHolder(holder: SongListRecyclerViewViewHolder?, position: Int) {
        holder?.setRepresentingSong(songs[position])
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
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongListRecyclerViewViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_songlist, parent, false)

        return SongListRecyclerViewViewHolder(itemView, onItemClickFunc)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.

     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = songs.size

}

class SongListRecyclerViewViewHolder(view: View, val onItemClickFunc: (song: Song) -> Unit) : RecyclerView.ViewHolder(view) {

    var song: Song? = null
        private set

    init {
        view.setOnClickListener {
            if (song != null) {
                onItemClickFunc(song!!)
            }
        }
    }

    val artistTextView: TextView = view.findViewById(R.id.songListLayoutArtist)
    val titleTextView: TextView = view.findViewById(R.id.songListLayoutTitle)
    val albumArtImageView: ImageView = view.findViewById(R.id.songListLayoutImage)

    fun setRepresentingSong(song: Song) {
        this.song = song
        this.artistTextView.text = song.artistName
        this.titleTextView.text = song.title
        Picasso.with(this.itemView.context).load(song.albumArt).into(albumArtImageView)
    }

}