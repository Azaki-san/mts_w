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
import javax.annotation.meta.TypeQualifierNickname


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

    fun getGame(gameId: Int) : ApiData? {
        val url = "https://store.steampowered.com/api/appdetails?appids=$gameId"
        val response = restTemplate.getForObject(url, ApiData::class.java)
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
                var screenshots = ""
                val description : String
                val developers = mutableListOf<String>()
                val releaseDate : String
                val price : String
                dataNode["name"]!!.let { name = it.asText()}
                dataNode["steam_appid"]!!.let { appId = it.asInt() }
                dataNode["screenshots"]!!.let {
                    screenshots = it[0]["path_thumbnail"].asText()
                }
                dataNode["about_the_game"]!!.let { description = it.asText() }
                dataNode["developers"]!!.let {
                    it.forEach { developer ->
                        developers.add(developer.asText())
                    }
                }
                dataNode["release_date"]!!["date"]!!.let { releaseDate = it.asText() }
                when (val priceOverview = dataNode["price_overview"]) {
                    null -> return null
                    else -> price = priceOverview["final"].asText()
                }

                return ApiData(appId, name, price, screenshots, description, developers, releaseDate)
            }
        }
        return null
    }
}
@JsonDeserialize(using = StoreApiResponseDeserializer::class)
data class ApiData(val id : Int,
                   val name: String,
                   val price: String,
                   val images: String,
                   val description: String,
                   val developers: List<String>,
                   val releaseDate: String)


data class Genres(val genres: MutableList<String>)
data class Game(val appid: Int, val name: String)

data class BuyGame(val username: String, val price: Int)