package com.steam.mts_widget.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

@Service
class SteamWebParser (private val apiParser: SteamDataService, private val mapper:ObjectMapper) {
    fun parseDiscountedGames(): String? {
        val doc: Document = Ksoup.parseGetRequestBlocking("https://store.steampowered.com/")
        val games = doc.select("div#tab_specials_content")[0]

        val discountedGames = games.childElementsList().filter {
            it.`is`("a")
        }
        val gameInfos : MutableList<ApiData> = mutableListOf()
        for (i in 0 until 11) {
            val id = discountedGames[i].attr("data-ds-appid").split(",")[0].toInt()
            val game = apiParser.getGame(id)
            if (game != null) {
                gameInfos.add(game)
                continue
            }
        }
        return mapper.writeValueAsString(gameInfos)
    }

    fun searchGameWithWord(word: String?): String {
        val doc = Ksoup.parseGetRequestBlocking("https://store.steampowered.com/search/?term=$word")
        val elements = doc.select("div#search_resultsRows a.search_result_row")
        val games : MutableList<ApiData> = mutableListOf()
        var i = 0
        var max = 10
        if (elements.isNotEmpty()) {
            while (i <= max){
                println(elements[i].attr("data-ds-appid"))
                println(elements[i].attr("href"))
                if (elements[i].attr("data-ds-appid") == "") {
                    max++
                    i++
                    continue
                }
                val ids = elements[i].attr("data-ds-appid").split(",")
                for (id in ids) {
                    val game = apiParser.getGame(id.toInt())
                    if (game != null) {
                        games.add(game)
                        break
                    }
                }
                i++
            }
            return mapper.writeValueAsString(games)
        } else {
            println("No elements found with the specified class.")
        }
        return ""
    }

    fun getGenres():  String {
        val doc = Ksoup.parseGetRequestBlocking("https://store.steampowered.com/")
        val elements = doc.select("div.home_page_gutter_block a.gutter_item")
        val genres : MutableList<String> = mutableListOf()
        var data : Genres? = null
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
            throw HttpClientErrorException(HttpStatus.NOT_FOUND, "No elements found with the specified class.")
        }

    }
}
