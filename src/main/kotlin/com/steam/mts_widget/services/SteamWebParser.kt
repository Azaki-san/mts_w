package com.steam.mts_widget.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import org.springframework.stereotype.Service

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
            gameInfos.add(
                apiParser.getGame(id)
            )
        }
        return mapper.writeValueAsString(gameInfos)

    }

    fun parseGameDetails() {
        TODO()
    }

}
