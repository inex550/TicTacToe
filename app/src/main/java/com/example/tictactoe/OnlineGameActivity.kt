package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.tictactoe.gameClasses.Cell
import com.example.tictactoe.gameClasses.OnlineGame
import com.example.tictactoe.gameClasses.Role
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_online_game.*
import java.lang.Exception

class OnlineGameActivity : AppCompatActivity() {

    lateinit var game: OnlineGame

    lateinit var imgCells: Array<ImageView>

    private var playerOneScore = 0
    private var playerTwoScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_game)

        val currentPlayer = intent.getSerializableExtra("role") as Role
        val gameId: String = intent.getStringExtra("gameId")!!

        playerOneTV.isEnabled = currentPlayer == Role.ONE
        playerTwoTV.isEnabled = currentPlayer == Role.TWO

        imgCells = arrayOf(cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8)

        game = OnlineGame(
            currentPlayer,
            Role.ONE,
            gameId,
            onEndGame = { role ->
                val msg = when(role) {
                    Role.NONE  -> "Ничья"
                    game.currentPlayer -> "Вы победили"
                    game.enemyPlayer  -> "Противник победил"
                    else -> ""
                }

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Игра окончена")
                    .setMessage(msg)
                    .setCancelable(false)
                    .create()
                dialog.show()

                Handler().postDelayed({
                    for (cell in imgCells)
                        cell.setImageResource(0)

                    game.restart()

                    if (role == game.currentPlayer)
                        playerOne_score.text = "${++playerOneScore}"
                    else if (role == game.enemyPlayer)
                        playerTwo_score.text = "${++playerTwoScore}"

                    playerOneTV.isEnabled = game.stepper == game.currentPlayer
                    playerTwoTV.isEnabled = game.stepper != game.currentPlayer

                    dialog.dismiss()
                }, 2000)
            },
            onDraw = { cellId, cell, change ->
                imgCells[cellId].setImageResource(
                    when (cell) {
                        Cell.CROSS -> R.drawable.ic_cross
                        Cell.OVAL -> R.drawable.ic_oval
                        Cell.NONE -> 0
                    }
                )

                if (change) {
                    playerOneTV.isEnabled = game.stepper != game.currentPlayer
                    playerTwoTV.isEnabled = game.stepper == game.currentPlayer
                }
            }
        )

        for (cellId in imgCells.indices)
            imgCells[cellId].setOnClickListener {
                playerStep(cellId)
            }

        game.dbGame.child("stop").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stop = snapshot.getValue<Boolean>() ?: return

                if (stop)
                    try {
                        AlertDialog.Builder(this@OnlineGameActivity)
                            .setTitle(":-/")
                            .setMessage("Противник отключился")
                            .setPositiveButton("Ок") { _, _ -> }
                            .setOnDismissListener {
                                game.dbGame.removeValue()

                                this@OnlineGameActivity.finish()
                            }
                            .create()
                            .show()
                    } catch (e: Exception) {
                        game.dbGame.child("stop").removeEventListener(this)
                    }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun playerStep(cellId: Int) {
        game.step(cellId)
    }

    override fun onDestroy() {
        super.onDestroy()

        game.dbGame.child("stop").setValue(true)
    }

    companion object {
        const val TAG = "OnlineTAG"
    }
}