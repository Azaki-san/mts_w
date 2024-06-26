package com.steam.mts_widget.controller

import com.steam.mts_widget.services.CurrencyConverterService
import com.steam.mts_widget.services.SBPService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.ceil

@CrossOrigin(origins = ["http://localhost:5173","http://localhost:5174"])
@RestController
class PayController(private val sbpService: SBPService, private val currencyConverterService: CurrencyConverterService) {
    @GetMapping("/buy")
    fun getBillLink(
        @RequestParam(required = true) priceWithoutFee: Int?,
        @RequestParam(required = true) username: String?
    ): ResponseEntity<String> {
        when {
            priceWithoutFee is Int && username is String -> {

                val priceUSDWithFee = currencyConverterService.getSteamUsdFromRub(priceWithoutFee)
                when {
                    priceUSDWithFee is Double -> {
                        val priceRUBWithFee = currencyConverterService.getMtsRubFromSteamUsd(priceUSDWithFee)
                        when {
                            priceRUBWithFee is Double -> {
                                val priceCeil = ceil(priceRUBWithFee).toInt()
                                val priceFinal = when {
                                    priceCeil < 350 -> 350
                                    priceCeil > 14950 -> 14950
                                    else -> priceCeil
                                }
                                val link: String? = sbpService.getBillLink(priceFinal, username)
                                return when {
                                    link is String -> {
                                        ResponseEntity
                                            .status(HttpStatus.PERMANENT_REDIRECT)
                                            .header("Location", link)
                                            .body("Redirecting to $link")
                                    }
                                    else -> {
                                        ResponseEntity
                                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .body("Cannot generate link")
                                    }
                                }
                            }
                            else -> {
                                return ResponseEntity
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error: MTS is down.")
                            }
                        }
                    }
                    else -> {
                        return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error: STEAM is down.")
                    }
                }
            }
            (priceWithoutFee !is Int && username !is String) -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Parameter 'priceWithoutFee' must be a valid integer. Parameter 'username' must be a valid string.")
            }
            priceWithoutFee !is Int -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Parameter 'priceWithoutFee' must be a valid integer.")
            }
            else -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Parameter 'username' must be a valid string.")
            }
        }
    }

//    @GetMapping("/bill_test")
//    fun getBillLink(): ResponseEntity<String> {
//        currencyConverterService.getMtsUsdToRub(1.0);
//        return ResponseEntity.status(HttpStatus.OK).body("Success!")
//    }
}