package andrewbastin.grace.singletons

import andrewbastin.grace.helpers.LastfmAPIHelper
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import co.metalab.asyncawait.async
import okhttp3.Request


object ArtistImageStore {

    private val STORE_PATH = "cache_artist_"

    private val onProgressSaves = mutableSetOf<String>()

    fun fetchImage(context: Context, artistName: String, offline: Boolean = false, postFetch: (image: Bitmap?, requestedArtistName: String) -> Unit) {

        // Check if the image is already cached
        if (checkIfCached(context, artistName)) {
            val file = context.getFileStreamPath("${STORE_PATH}$artistName")
            Log.i("Fetch", "Has cached file at path ${STORE_PATH}$artistName, name: $artistName")

            var bitmap: Bitmap? = null

            file.inputStream().use {
                try {
                    bitmap = BitmapFactory.decodeStream(it)
                } catch (e: Exception) {
                }
            }

            postFetch(bitmap, artistName)

        } else if (!offline) {
            async {
                await {

                    val artistImageURL = LastfmAPIHelper.getArtistImageURL(artistName, GraceHttpClient.client)
                    Log.i("Fetch", "Fetched URL : $artistImageURL, name: $artistName")

                    try {
                        if (artistImageURL != null) {

                            val request = Request.Builder().url(artistImageURL).build()
                            val response = GraceHttpClient.client.newCall(request).execute()

                            Log.i("Fetch", "Sent request, name: $artistName")

                            val inputStream = response.body()?.byteStream()
                            Log.i("Fetch", "Input Stream, name: $artistName")

                            inputStream?.use {
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                Log.i("Fetch", "Decoded, name: $artistName")

                                postFetch(bitmap, artistName)

                                if (!onProgressSaves.contains(artistName)) {

                                    onProgressSaves.add(artistName)

                                    val outputStream = context.getFileStreamPath("${STORE_PATH}$artistName").outputStream()

                                    outputStream.use {
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                    }

                                    Log.i("Fetch", "Saved, name: $artistName")

                                    onProgressSaves.remove(artistName)

                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }

        }

    }

    fun getCachedImage(context: Context, artistName: String): Bitmap? {
        val file = context.getFileStreamPath("$STORE_PATH$artistName")
        return if (file == null || !file.exists() || onProgressSaves.contains(artistName)) {
            Log.i("S", "null")
            null
        } else {
            Log.i("S", "pass")
            file.inputStream().use { BitmapFactory.decodeStream(it) }
        }
    }

    private fun checkIfCached(context: Context, artistName: String): Boolean {
        val file = context.getFileStreamPath("${STORE_PATH}$artistName")
        return !(file == null || !file.exists())
    }

}