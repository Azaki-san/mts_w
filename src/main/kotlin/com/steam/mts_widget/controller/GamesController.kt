package com.steam.mts_widget.controller

import com.steam.mts_widget.services.Game
import com.steam.mts_widget.services.SteamDataService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException


@RestController
class GamesController(private val steamDataService: SteamDataService){
    @GetMapping("/help")
    fun copyright(): String {
        return "©2024 Techaas. All rights reserved"
    }

    @GetMapping("/games")
    fun getAllGames(): ResponseEntity<List<Game>> {
        return ResponseEntity.ok(steamDataService.getAllDiscountedGames())
    }

    @GetMapping("/game")
    fun getGame(@RequestParam(required = true) gameId: String?): ResponseEntity<Any> {
        if (gameId == null || !gameId.matches(Regex("\\d+"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'gameId' must be a valid integer")
        }
        return try {
            val gameIdInt = gameId.toInt()
            steamDataService.getGame(gameIdInt)
            ResponseEntity.ok("Game details fetched for app ID: $gameId")
        } catch (e: HttpClientErrorException) {
            ResponseEntity.status(e.statusCode).body("Error fetching game details: ${e.message}")
        }
    }
}