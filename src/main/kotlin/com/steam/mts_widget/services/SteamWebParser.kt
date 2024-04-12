package com.steam.mts_widget.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.select.Elements
import org.springframework.stereotype.Service

@Service
class SteamWebParser(private val apiParser: SteamDataService, private val mapper: ObjectMapper) {
    fun parseDiscountedGames(): String? {
        val doc: Document = Ksoup.parseGetRequestBlocking("https://store.steampowered.com/")
        val games = doc.select("div#tab_specials_content")[0]

        val discountedGames = games.childElementsList().filter {
            it.`is`("a")
        }
        val gameInfos: MutableList<ApiData> = mutableListOf()
        for (i in 0 until 11) {
            val id = discountedGames[i].attr("data-ds-appid").split(",")[0].toInt()
            gameInfos.add(
                apiParser.getGame(id)
            )
        }
        return mapper.writeValueAsString(gameInfos)
    }

    fun parseGameDetails() {
        TODO()
    }

    fun parseGameByGenre(genre: String): String? {
        val genre2 = genre.lowercase()
        val url = "https://games-stats.com/steam/?tag=$genre2"
        val doc = Ksoup.parseGetRequestBlocking(url)
        val games = doc.select("div.steam__table-wrapper a[href*=store.steampowered.com/app/]")
        var count = 0
        val gameInfos: MutableList<ApiData> = mutableListOf()
        for (game in games) {
            val href = game.attr("href")
            val appIdMatch = Regex("/app/(\\d+)/").find(href)
            if (appIdMatch != null) {
                val appId = appIdMatch.groupValues[1].toInt()
                try {
                    gameInfos.add(apiParser.getGame(appId))
                } catch (e: Exception) {
                    continue
                }
                count++
                if (count >= 10) break
            }
        }
        return mapper.writeValueAsString(gameInfos)
    }



    fun getGenres(): String {
        val doc = Ksoup.parseGetRequestBlocking("https://store.steampowered.com/")
        val elements = doc.select("div.home_page_gutter_block a.gutter_item")
        val genres: MutableList<String> = mutableListOf()
        var data: Genres? = null
        if (elements.isNotEmpty()) {
            for (element in elements) {
                val href = element.attr("href")
                if (href.contains("store.steampowered.com/tags")) {
                    print(element)
                    genres.add(element.text())
                }
            }
            data = Genres(genres)
            return mapper.writeValueAsString(data)
        } else {
            println("No elements found with the specified class.")
        }
        return ""
    }
}
