package com.example.task_audioplayer.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.BIND_AUTO_CREATE
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task_audioplayer.MusicPlayerService
import com.example.task_audioplayer.R
import com.example.task_audioplayer.data.Song
import com.example.task_audioplayer.data.songsList
import com.example.task_audioplayer.ui.Adapter.AllSongsAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SongsFragment : Fragment() {

//    var adapter : AllSongsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclearview)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.hasFixedSize()

        val adapter = AllSongsAdapter(requireContext())

        recyclerView.adapter = adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

}