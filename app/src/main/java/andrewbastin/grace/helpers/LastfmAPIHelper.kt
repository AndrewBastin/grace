package andrewbastin.grace.helpers

import andrewbastin.grace.extensions.json
import andrewbastin.grace.music.data.Album
import andrewbastin.grace.music.data.Artist
import andrewbastin.grace.singletons.GraceHttpClient
import andrewbastin.grace.utils.aboveAPI
import android.text.Html
import android.text.Spanned
import android.text.SpannedString
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object LastfmAPIHelper {
    val API_KEY = "1b922661b02aa7a876ce5a551d016e1c"

    val API_BASE_URL = "http://ws.audioscrobbler.com/2.0/"

    fun getArtistImageURL(artistName: String, httpClient: OkHttpClient): String? {

        try {

            val request = Request.Builder().url(generateURL("artist.getinfo", listOf("api_key" to API_KEY, "format" to "json", "artist" to artistName))).build()

            val response = httpClient.newCall(request).execute()

            val data = response?.body()?.string()

            val rootObj = JSONObject(data)
            val artistObj = rootObj.getJSONObject("artist")
            val imageArray = artistObj.getJSONArray("image")
            val extraLargeImageObj = imageArray.getJSONObject(3)

            return extraLargeImageObj.getString("#text")

        } catch (e: Exception) {
            return null
        }
    }

    fun getAlbumBio(album: Album): String {
        val request = Request.Builder().url(generateURL("album.getinfo", listOf(
                "api_key" to API_KEY,
                "format" to "json",
                "artist" to album.artistName,
                "album" to album.title
        ))).build()

        val response = GraceHttpClient.client.newCall(request).execute()
        val data = response.body()?.string()

        val rootObj = response.body()?.json()

        val albumObj = rootObj?.getJSONObject("album")
        val wikiObj = albumObj?.getJSONObject("wiki")

        return wikiObj?.getString("content") ?: ""
    }

    fun getArtistBio(artist: Artist): String {
        val request = Request.Builder().url(generateURL("artist.getinfo", listOf(
                "api_key" to API_KEY,
                "format" to "json",
                "artist" to artist.name
        ))).build()

        val response = GraceHttpClient.client.newCall(request).execute()

        val rootObj = response.body()?.json()
        val artistObj = rootObj?.getJSONObject("artist")
        val bioObj = artistObj?.getJSONObject("bio")

        return bioObj?.getString("content") ?: ""
    }

    private fun generateURL(method: String, params: List<Pair<String, String>>): String {
        val builder = StringBuilder()
        builder.append(API_BASE_URL)
        builder.append("?method=$method")
        for ((paramName, paramVal) in params) {
            builder.append("&$paramName=$paramVal")
        }
        return builder.toString()
    }
}
