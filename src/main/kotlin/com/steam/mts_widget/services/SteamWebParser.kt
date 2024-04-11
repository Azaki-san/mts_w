package com.steam.mts_widget.services

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document


class SteamWebParser {
    fun parseDiscountedGames() {
        val html = "<html><head><title>One</title></head><body>Two</body></html>"
        val doc: Document = Ksoup.parse(html = html)

        println("title => ${doc.title()}") // One
        println("bodyText => ${doc.body().text()}") // Two
    }

    fun parseGameDetails() {
        TODO()
    }
}
