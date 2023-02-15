package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.TrackName)
    private val artistName: TextView = itemView.findViewById(R.id.ArtistName)
    private val trackLength: TextView = itemView.findViewById(R.id.TrackLength)
    private val trackImage: ImageView = itemView.findViewById(R.id.TrackImage)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackLength.text = model.trackTime
        Glide.with(trackImage)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.close)
            .centerCrop()
            .transform(RoundedCorners(20))
            .into(trackImage)
    }
}