package com.example.tictactoe.gameClasses

import android.util.Log
import com.example.tictactoe.OnlineGameActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class OnlineGame(
    val currentPlayer: Role,
    var stepper: Role,
    gameId: String,
    val onEndGame: (Role) -> Unit,
    val onDraw: (Int, Cell, Boolean) -> Unit
) {
    private var figure: Cell = if (currentPlayer == Role.ONE) Cell.CROSS else Cell.OVAL
    private val cells = Array(9) {Cell.NONE}

    val enemyPlayer = if (currentPlayer == Role.ONE) Role.TWO else Role.ONE

    var crossRole = stepper
    var ovalRole = if (stepper == Role.ONE) Role.TWO else Role.ONE

    val dbGame = Firebase.database.reference.child("games").child(gameId)

    init {
        for (i in 0 until 9)
            dbGame.child("game").child(i.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cell = snapshot.getValue<Cell>() ?: return

                    if (cell == Cell.NONE) {
                        onDraw(i, Cell.NONE, false)
                        cells[i] = cell
                    } else {
                        if (cell == figure) return

                        cells[i] = cell
                        onDraw(i, cell, true)

                        when {
                            checkWin(cell) -> onEndGame(if (cell == Cell.CROSS) crossRole else ovalRole)
                            checkEnd() -> onEndGame(Role.NONE)
                            else -> {
                                stepper = if (stepper == Role.ONE) Role.TWO else Role.ONE
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun step(cellId: Int) {
        if (currentPlayer != stepper || cells[cellId] != Cell.NONE) return

        dbGame.child("game").child(cellId.toString()).setValue(figure)

        cells[cellId] = figure
        onDraw(cellId, figure, true)

        Log.d(OnlineGameActivity.TAG, "Step")

        when {
            checkWin(figure) -> onEndGame(if (figure == Cell.CROSS) crossRole else ovalRole)
            checkEnd() -> onEndGame(Role.NONE)
            else -> {
                stepper = if (stepper == Role.ONE) Role.TWO else Role.ONE
            }
        }
    }

    fun restart() {
        dbGame.child("game").setValue(List(9) { Cell.NONE })

        stepper = if (stepper == Role.ONE) Role.TWO else Role.ONE
        figure = if (stepper == currentPlayer) {
            crossRole = currentPlayer
            ovalRole = enemyPlayer
            Cell.CROSS
        } else {
            crossRole = enemyPlayer
            ovalRole = currentPlayer
            Cell.OVAL
        }
    }

    private fun checkWin(role: Cell): Boolean {
        return if (cells[0]==role && cells[1]==role && cells[2]==role)  true
        else if   (cells[3]==role && cells[4]==role && cells[5]==role)  true
        else if   (cells[6]==role && cells[7]==role && cells[8]==role)  true
        else if   (cells[0]==role && cells[3]==role && cells[6]==role)  true
        else if   (cells[1]==role && cells[4]==role && cells[7]==role)  true
        else if   (cells[2]==role && cells[5]==role && cells[8]==role)  true
        else if   (cells[0]==role && cells[4]==role && cells[8]==role)  true
        else       cells[2]==role && cells[4]==role && cells[6]==role
    }

    private fun checkEnd(): Boolean {
        for (cell in cells) {
            if (cell == Cell.NONE)
                return false
        }

        return true
    }
}