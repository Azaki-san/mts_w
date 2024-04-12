package com.steam.mts_widget.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonObject
import com.steam.mts_widget.services.Game
import com.steam.mts_widget.services.SteamDataService
import com.steam.mts_widget.services.SteamWebParser
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@CrossOrigin(origins = [ "http://localhost:5173","http://localhost:5174"])
@RestController
class GamesController(
    private val steamDataService: SteamDataService,
    private val steamWebParser: SteamWebParser,
    private val mapper: ObjectMapper
) {
    @GetMapping("/help")
    fun copyright(): ResponseEntity<String> {
        return ResponseEntity.ok("©2024 Techaas. All rights reserved")
    }

    @GetMapping("/games", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDiscountedGames(): ResponseEntity<String> {
        return ResponseEntity.ok(steamWebParser.parseDiscountedGames())
    }

    @GetMapping("/game/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGame(@PathVariable(required = true) id: String?): ResponseEntity<Any> {
        if (id == null || !id.matches(Regex("\\d+"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'gameId' must be a valid integer")
        }
        return try {
            val gameIdInt = id.toInt()
            return ResponseEntity.ok(
                mapper.writeValueAsString(steamDataService.getGame(gameIdInt))
            )
        } catch (e: HttpClientErrorException) {
            ResponseEntity.status(e.statusCode).body("Error fetching game details: ${e.message}")
        }
    }

    @GetMapping("/search/{word}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchGame(@PathVariable word: String?): ResponseEntity<String>{
        return ResponseEntity.ok(steamWebParser.searchGameWithWord(word))
    }

    @GetMapping("/genres", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGamesByGenre(@RequestParam(required = true) genre: String?): ResponseEntity<Any> {
        return ResponseEntity.ok(steamWebParser.getGenres())
    }









    @GetMapping("/games/{genre}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByGenre(@PathVariable genre: String?): ResponseEntity<String> {
        return ResponseEntity.ok(genre?.let { steamWebParser.parseGameByGenre(it) })
    }
}