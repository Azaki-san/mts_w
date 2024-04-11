package com.steam.mts_widget.services
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringToJsonService {
    fun jsonStringToMap(jsonString: String): Map<String, Any> {
        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}