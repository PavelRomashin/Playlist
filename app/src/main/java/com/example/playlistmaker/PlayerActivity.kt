package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val backButton = findViewById<Button>(R.id.backButton)
        val trackImage = findViewById<ImageView>(R.id.TrackImage)
        val trackName = findViewById<TextView>(R.id.TrackName)
        val artistName = findViewById<TextView>(R.id.ArtistName)
        val trackLength = findViewById<TextView>(R.id.TrackLengthValue)
        val trackLengthGroup =
            findViewById<androidx.constraintlayout.widget.Group>(R.id.TrackLengthGroup)
        val albumName = findViewById<TextView>(R.id.AlbumName)
        val albumGroup = findViewById<androidx.constraintlayout.widget.Group>(R.id.AlbumGroup)
        val releaseYearValue = findViewById<TextView>(R.id.releaseYearValue)
        val releaseYearGroup =
            findViewById<androidx.constraintlayout.widget.Group>(R.id.releaseYearGroup)
        val genreValue = findViewById<TextView>(R.id.genreValue)
        val genreGroup = findViewById<androidx.constraintlayout.widget.Group>(R.id.genreGroup)
        val countryName = findViewById<TextView>(R.id.countryName)
        val countryGroup = findViewById<androidx.constraintlayout.widget.Group>(R.id.countryGroup)
        //Задел на будущие спринты
        val addTrack = findViewById<ImageButton>(R.id.addTrack)
        val playButton = findViewById<ImageButton>(R.id.playTrack)
        val likeButton = findViewById<ImageButton>(R.id.likeTrack)

        val json = intent.getStringExtra(TRACK)
        val track = Gson().fromJson(json, Track::class.java)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackLength.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        Glide.with(trackImage).load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_player).centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.placeholder_size)))
            .into(trackImage)

        if (track.trackTime != 0) {
            trackLength.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        } else {
            trackLengthGroup.visibility = View.GONE
        }

        if (track.collectionName.isNotEmpty()) {
            albumName.text = track.collectionName
        } else {
            albumGroup.visibility = View.GONE
        }

        if (track.releaseDate.isNotEmpty()) {
            releaseYearValue.text = track.releaseDate.split("-")[0]
        } else {
            releaseYearGroup.visibility = View.GONE
        }

        if (track.primaryGenreName.isNotEmpty()) {
            genreValue.text = track.primaryGenreName
        } else {
            genreGroup.visibility = View.GONE
        }

        if (track.country.isNotEmpty()) {
            countryName.text = track.country
        } else {
            countryGroup.visibility = View.GONE
        }

        backButton.setOnClickListener {
            this.finish()
        }


    }


}