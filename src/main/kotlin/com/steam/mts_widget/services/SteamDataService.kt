package com.steam.mts_widget.services

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


@Service
class SteamDataService(private val restTemplate: RestTemplate) {
    fun getAllDiscountedGames(): List<Game> {
        TODO("Implement parsing Steam Store web, only discounted games")
    }

    fun getGenre (genre: Int): List<Game> {
        TODO("Implement parsing Steam Store web from search page ")
    }

    fun getGamesByGenre(genre: Int): List<Game> {
        TODO("Implement parsing Steam Store web from search page ")
    }

    fun searchGame(gameName: String): List<Game> {
        TODO("Implement parsing Steam Store web from search page ")
    }

    fun getGame(gameId: Int) : ApiData {
        val url = "https://store.steampowered.com/api/appdetails?appids=$gameId"
        val response = restTemplate.getForObject(url, ApiData::class.java)
        if (response == null || response.name.isEmpty()) {
            throw HttpClientErrorException(HttpStatus.NOT_FOUND, "Game not found")
        }
        return response
    }
}

class StoreApiResponseDeserializer : JsonDeserializer<ApiData>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ApiData? {
        val node: JsonNode = p.codec.readTree(p)
        var data : ApiData? = null

        node.fields().forEach { (_, value) ->
            val success = value["success"].asBoolean()

            if (success){
                val dataNode = value["data"]
                val name : String
                val appId : Int
                val screenshots = mutableListOf<String>()
                val description : String
                val developers = mutableListOf<String>()
                val releaseDate : String
                val price : String

                dataNode["name"]!!.let { name = it.asText()}
                dataNode["steam_appid"]!!.let { appId = it.asInt() }
                dataNode["screenshots"]!!.let {
                    it.forEach { screenshot ->
                        val path = screenshot["path_thumbnail"].asText()
                        screenshots.add(path)
                    }
                }
                dataNode["about_the_game"]!!.let { description = it.asText() }
                dataNode["developers"]!!.let {
                    it.forEach { developer ->
                        developers.add(developer.asText())
                    }
                }
                dataNode["release_date"]!!["date"]!!.let { releaseDate = it.asText() }
                dataNode["price_overview"]!!["final"]!!.let { price = it.asText() }

                data = ApiData(appId, name, price, screenshots, description, developers, releaseDate)
            }
        }
        return data
    }
}
@JsonDeserialize(using = StoreApiResponseDeserializer::class)
data class ApiData(val id : Int,
                   val name: String,
                   val price: String,
                   val images: List<String>,
                   val description: String,
                   val developers: List<String>,
                   val releaseDate: String)


data class Genres(val genres: MutableList<String>)
data class Game(val appid: Int, val name: String)
data class SteamApiResponse(val applist: AppList)
data class AppList(val apps: List<Game>)