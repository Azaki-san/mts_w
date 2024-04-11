package com.steam.mts_widget.services

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class SteamDataService(private val restTemplate: RestTemplate, private val stringToJson: StringToJsonService) {
    fun getAllGames(): List<Game> {
        val url = "https://api.steampowered.com/ISteamApps/GetAppList/v2"
        val response = restTemplate.getForObject(url, SteamApiResponse::class.java)
        val games = response?.applist?.apps
            ?.filter { it.name.contains("hentai", ignoreCase = true).not() }
            ?.filter { it.name.isNotEmpty() }
            ?.map { it.copy(name = it.name.trim()) }
            ?.sortedBy { it.name }
            ?: emptyList()

        return games
    }

    fun getGame(gameId: Int): Map<String, Any>? {
        val response: String? = restTemplate.getForObject("https://store.steampowered.com/api/appdetails?appids=$gameId",
            String::class.java)
        val map: Map<String, Any>? = response?.let { stringToJson.jsonStringToMap(it) }
        return map?.get(gameId.toString()) as? Map<String, Any>
    }
}

data class Game(val appid: Int, val name: String)
data class SteamApiResponse(val applist: AppList)
data class AppList(val apps: List<Game>)