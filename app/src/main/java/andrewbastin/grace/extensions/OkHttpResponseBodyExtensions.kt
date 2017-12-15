package andrewbastin.grace.extensions

import okhttp3.ResponseBody
import org.json.JSONObject

fun ResponseBody.json(): JSONObject = JSONObject(this.string())

