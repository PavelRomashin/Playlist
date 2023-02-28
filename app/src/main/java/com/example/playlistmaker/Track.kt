package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: String, // Продолжительность трека
    @SerializedName("artworkUrl100") val artworkUrl100: String  // Ссылка на изображение обложки
)

val trackLibrary = ArrayList<Track>()
