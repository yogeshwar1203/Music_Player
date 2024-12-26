package com.example.task_audioplayer.ui.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task_audioplayer.R
import com.example.task_audioplayer.data.songsList
import com.example.task_audioplayer.ui.screens.CurrentSong

class AllSongsAdapter(private val context: Context) : RecyclerView.Adapter<AllSongsAdapter.MyViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewModel {
        val view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
        return MyViewModel(view)
    }

    override fun onBindViewHolder(holder: MyViewModel, position: Int) {
        val song = songsList[position]

        Log.d("position" , position.toString())
        holder.tvTitle.text = song.title
        Log.d("title" , song.title)
        holder.tvArtist.text = song.artist

        holder.item.setOnClickListener {
            val intent = Intent(context, CurrentSong::class.java)
            intent.putExtra("song", position)
            context.startActivity(intent)
        }
    }

    class MyViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val item: LinearLayout = itemView.findViewById(R.id.song_item)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }
}
