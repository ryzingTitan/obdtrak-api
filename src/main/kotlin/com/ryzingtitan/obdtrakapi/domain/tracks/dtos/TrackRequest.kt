package com.ryzingtitan.obdtrakapi.domain.tracks.dtos

data class TrackRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)
