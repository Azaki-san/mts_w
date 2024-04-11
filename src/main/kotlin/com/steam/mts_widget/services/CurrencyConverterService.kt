package com.steam.mts_widget.services

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class CurrencyConverterService(private val restTemplate: RestTemplate) {
    fun getSteamUsdToRub(money: Int): Double? {
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

    fun getMtsUsdToRub(money: Double): Double? {
        return 0.0
    }
}

data class Meta(val count: Int)

data class CurrencyData(val currency_pair: String, val close_price: Double, val created_at: String)

data class CurrencyResponse(val meta: Meta, val data: List<CurrencyData>)