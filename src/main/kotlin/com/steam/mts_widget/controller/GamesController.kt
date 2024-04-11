package com.steam.mts_widget.controller

import com.google.gson.JsonObject
import com.steam.mts_widget.services.Game
import com.steam.mts_widget.services.SteamDataService
import com.steam.mts_widget.services.SteamWebParser
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@CrossOrigin(origins = ["http://localhost:5174"], maxAge = 3600)
@RestController
class GamesController(private val steamDataService: SteamDataService, private val steamWebParser: SteamWebParser) {
    @GetMapping("/help")
    fun copyright(): ResponseEntity<String> {
        return ResponseEntity.ok("©2024 Techaas. All rights reserved")
    }

    @GetMapping("/games", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDiscountedGames(): ResponseEntity<String> {
        return ResponseEntity.ok(steamWebParser.parseDiscountedGames())
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

    @GetMapping("/genres", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGamesByGenre(@RequestParam(required = true) genre: String?): ResponseEntity<Any> {
        return ResponseEntity.ok(steamWebParser.getGenres())
    }
}