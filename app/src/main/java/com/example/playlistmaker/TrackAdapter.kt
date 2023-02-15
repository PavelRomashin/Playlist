package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(private val trackLibrary: ArrayList<Track>) : RecyclerView.Adapter<TrackHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(trackLibrary[position])
    }

    override fun getItemCount(): Int {
        return trackLibrary.size
    }
}