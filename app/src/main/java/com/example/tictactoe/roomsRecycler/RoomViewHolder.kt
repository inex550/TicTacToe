package com.example.tictactoe.roomsRecycler

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tictactoe.OnlineGameActivity
import com.example.tictactoe.onlineModels.Room
import com.example.tictactoe.R
import com.example.tictactoe.gameClasses.Role
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RoomViewHolder(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.rooms_list_item, parent, false)) {

    private var rTitleView: TextView? = null

    init {
        rTitleView = itemView.findViewById(R.id.room_title)
    }

    fun bind(room: Room) {
        rTitleView?.text = room.title
    }
}