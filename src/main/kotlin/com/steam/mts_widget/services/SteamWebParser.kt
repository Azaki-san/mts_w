package com.steam.mts_widget.services

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class SteamWebParser {
    fun parseDiscountedGames(): Int {
        val doc: Document = Ksoup.parseGetRequestBlocking("https://store.steampowered.com/")
        val games = doc.select("div#tab_specials_content")[0]
        games.childElementsList().filter {
            it.`is`("a")
        }.forEach {
            println(it.attr("data-ds-appid"))
        }
        return 1

    }

    fun parseGameDetails() {
        TODO()
    }

}
