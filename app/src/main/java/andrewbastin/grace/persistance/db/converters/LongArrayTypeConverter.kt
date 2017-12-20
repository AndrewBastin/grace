package andrewbastin.grace.persistance.db.converters

import android.arch.persistence.room.TypeConverter
import org.json.JSONArray
import org.json.JSONObject

class LongArrayTypeConverter {

    @TypeConverter
    fun fromLongArray(arr: Array<Long>) = JSONArray(arr.toList()).toString()

    @TypeConverter
    fun toLongArray(jsonString: String): Array<Long> {

        val res = mutableListOf<Long>()

        val jsonArray = JSONArray(jsonString)

        (0 until jsonArray.length()).mapTo(res) { jsonArray.getLong(it) }

        return res.toTypedArray()
    }
}