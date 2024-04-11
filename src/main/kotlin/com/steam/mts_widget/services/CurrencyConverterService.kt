package com.steam.mts_widget.services

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.jsoup.Jsoup

@Service
class CurrencyConverterService(private val restTemplate: RestTemplate) {
    fun getSteamUsdFromRub(money: Int): Double? {
        val url = "https://api.steam-currency.ru/currency/USD:RUB"
        val response = restTemplate.getForObject(url, CurrencyResponse::class.java)
        return when {
            response is CurrencyResponse -> {
                if (response.meta.count != 0) {
                    val usdPrice: Double = response.data.last().close_price
                    money / usdPrice
                } else {
                    null
                }
            } else -> {
                null
            }
        }
    }

    fun getMtsRubFromSteamUsd(money: Double): Double? {
        val doc: Document = Ksoup.parseGetRequestBlocking(url = "https://www.mtsbank.ru/")
        val sellingPriceElement = doc.select(".Wrapper-sc-1vydk7-0.BfQtf")[2]
        val sellingPrice = sellingPriceElement.text()
        return try {
            val sellingPriceFormatted = sellingPrice.replace(",", ".")
            sellingPriceFormatted.toDouble() * money
        } catch (e: NumberFormatException) {
            println("Error converting selling price to double: ${e.message}")
            null
        }
    }
}

data class Meta(val count: Int)

data class CurrencyData(val currency_pair: String, val close_price: Double, val created_at: String)

data class CurrencyResponse(val meta: Meta, val data: List<CurrencyData>)