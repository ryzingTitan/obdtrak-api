package com.ryzingtitan.datalogapi.presentation.controllers

import com.ryzingtitan.datalogapi.domain.tracks.dtos.Track
import com.ryzingtitan.datalogapi.domain.tracks.services.TrackService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping(path = ["/api/tracks"])
class TrackController(
    private val trackService: TrackService,
) {
    @GetMapping
    fun getTracks(): Flow<Track> = trackService.getAll()

    @PostMapping
    suspend fun createTrack(
        @RequestBody track: Track,
        response: ServerHttpResponse,
        exchange: ServerWebExchange,
    ) {
        val trackId = trackService.create(track)

        response.headers.add(
            HttpHeaders.LOCATION,
            "${exchange.request.uri}/$trackId",
        )
        response.statusCode = HttpStatus.CREATED
    }

    @PutMapping("/{trackId}")
    suspend fun updateTrack(
        @PathVariable(name = "trackId") trackId: Int,
        @RequestBody track: Track,
    ) {
        trackService.update(track.copy(id = trackId))
    }

    @DeleteMapping("/{trackId}")
    suspend fun deleteTrack(
        @PathVariable(name = "trackId") trackId: Int,
    ) {
        trackService.delete(trackId)
    }
}
