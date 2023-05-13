package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private lateinit var playButton: ImageButton
    private lateinit var backButton: Button
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackLength: TextView
    private lateinit var trackLengthGroup: androidx.constraintlayout.widget.Group
    private lateinit var albumName: TextView
    private lateinit var albumGroup: androidx.constraintlayout.widget.Group
    private lateinit var releaseYearValue: TextView
    private lateinit var releaseYearGroup: androidx.constraintlayout.widget.Group
    private lateinit var genreValue: TextView
    private lateinit var genreGroup: androidx.constraintlayout.widget.Group
    private lateinit var countryName: TextView
    private lateinit var countryGroup: androidx.constraintlayout.widget.Group
    private lateinit var addTrack: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var track: Track
    private lateinit var handler: Handler
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT


    private val run = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING)
                trackLength.text =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
            handler.postDelayed(this, DELAY)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initialization()
        preparePlayer()

        val json = intent.getStringExtra(TRACK)
        val track = Gson().fromJson(json, Track::class.java)
        handler = Handler(Looper.getMainLooper())

        playButton.setOnClickListener {
            playbackControl()
        }

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
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    private fun initialization(){
        playButton = findViewById(R.id.playTrack)
        backButton = findViewById(R.id.backButton)
        trackImage = findViewById(R.id.TrackImage)
        trackName = findViewById(R.id.TrackName)
        artistName = findViewById(R.id.ArtistName)
        trackLength = findViewById(R.id.TrackLengthValue)
        trackLengthGroup =
            findViewById(R.id.TrackLengthGroup)
        albumName = findViewById(R.id.AlbumName)
        albumGroup = findViewById(R.id.AlbumGroup)
        releaseYearValue = findViewById(R.id.releaseYearValue)
        releaseYearGroup =
            findViewById(R.id.releaseYearGroup)
        genreValue = findViewById(R.id.genreValue)
        genreGroup = findViewById(R.id.genreGroup)
        countryName = findViewById(R.id.countryName)
        countryGroup = findViewById(R.id.countryGroup)

    }
    private fun preparePlayer() {
         mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(run)
           trackLength.text = DEFAULT_MM_SS
            playerState = STATE_PREPARED
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
        handler.post(run)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_track)
        playerState = STATE_PAUSED
        handler.removeCallbacks(run)
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    companion object {
        private const val DEFAULT_MM_SS = "00:00"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }

}