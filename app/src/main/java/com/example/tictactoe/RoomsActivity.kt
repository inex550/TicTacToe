package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tictactoe.gameClasses.Cell
import com.example.tictactoe.gameClasses.Role
import com.example.tictactoe.onlineModels.Room
import com.example.tictactoe.roomsRecycler.RoomsAdapter
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_rooms.*

class RoomsActivity : AppCompatActivity() {

    private val rooms = mutableListOf<Room>()

    private lateinit var dbRooms: DatabaseReference
    private lateinit var dbGames: DatabaseReference

    private lateinit var adapter: RoomsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        adapter = RoomsAdapter(rooms, this)

        rooms_recycler_view.layoutManager = LinearLayoutManager(this)
        rooms_recycler_view.adapter = adapter

        dbRooms = Firebase.database.reference.child("rooms")
        dbGames = Firebase.database.reference.child("games")

        dbRooms.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val room = snapshot.getValue<Room>()!!
                rooms.add(0, room)
                adapter.notifyDataSetChanged()

                if (roomsNothing.visibility == View.VISIBLE) roomsNothing.visibility = View.GONE
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val room = snapshot.getValue<Room>()!!
                rooms.remove(room)
                adapter.notifyDataSetChanged()

                if (rooms.size == 0) roomsNothing.visibility = View.VISIBLE
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "dbRooms:onCancelled", error.toException())
                Toast.makeText(this@RoomsActivity, "Failed to load rooms.", Toast.LENGTH_SHORT).show()
            }

        })

        val waitDialog = WaitDialog(this)

        add_room_btn.setOnClickListener {
            val v = layoutInflater.inflate(R.layout.create_room, null)

            AlertDialog.Builder(this)
                .setTitle("Название комнаты")
                .setView(v)
                .setPositiveButton("Ок") { _, _ ->
                    val title = v.findViewById<EditText>(R.id.titleEditText).text.toString()

                    if (title.isNotEmpty()) {
                        val key = dbRooms.push().key
                        val room = Room(key!!, title, false)
                        dbRooms.child(key).setValue(room)

                        waitDialog.startWaitDialog()

                        dbRooms.child(key).child("ready").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val ready = snapshot.getValue<Boolean>()

                                if (ready != null && ready) {
                                    waitDialog.dismissDialog()

                                    val cells = List(9) { Cell.NONE }
                                    dbGames.child(key).child("game").setValue(cells)
                                    dbGames.child(key).child("stop").setValue(false)

                                    rooms.remove(room)

                                    val intent = Intent(this@RoomsActivity, OnlineGameActivity::class.java)
                                    intent.putExtra("gameId", key)
                                    intent.putExtra("role", Role.ONE)
                                    startActivity(intent)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })

                    }
                }
                .create()
                .show()
        }
    }

    companion object {
        const val TAG = "MainActivityTAG"
    }
}