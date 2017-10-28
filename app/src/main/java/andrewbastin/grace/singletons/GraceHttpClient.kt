package andrewbastin.grace.singletons

import android.content.Context
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

object GraceHttpClient {
    val CACHE_DIR_RELATIVE = "/cache/http/"
    val CACHE_SIZE_MB = 10

    lateinit var client: OkHttpClient
        private set

    fun initialize(context: Context) {
        client = OkHttpClient().newBuilder().cache(Cache(File("${context.applicationInfo.dataDir}$CACHE_DIR_RELATIVE"), (CACHE_SIZE_MB * 1024 * 1024).toLong())).build()
    }
}