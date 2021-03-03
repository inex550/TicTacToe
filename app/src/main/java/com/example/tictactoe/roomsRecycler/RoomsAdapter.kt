package com.example.tictactoe.roomsRecycler

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tictactoe.OnlineGameActivity
import com.example.tictactoe.gameClasses.Role
import com.example.tictactoe.onlineModels.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RoomsAdapter(
    private val list: MutableList<Room>,
    private val context: Activity
) : RecyclerView.Adapter<RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RoomViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room: Room = list[position]

        holder.itemView.setOnClickListener {
            Firebase.database.reference.child("rooms").child(room.id!!).child("ready").setValue(true)
            Firebase.database.reference.child("games").child(room.id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val exist = snapshot.exists()

                    if (exist) {
                        Firebase.database.reference.child("rooms").child(room.id).removeValue()

                        list.remove(room)

                        Firebase.database.reference.child("games").child(room.id).removeEventListener(this)

                        val intent = Intent(context, OnlineGameActivity::class.java)
                        intent.putExtra("gameId", room.id)
                        intent.putExtra("role", Role.TWO)
                        ContextCompat.startActivity(context, intent, null)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        holder.bind(room)
    }

    override fun getItemCount(): Int = list.size

}