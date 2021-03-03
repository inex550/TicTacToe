package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.tictactoe.gameClasses.Cell
import com.example.tictactoe.gameClasses.OfflineGame
import com.example.tictactoe.gameClasses.Role
import kotlinx.android.synthetic.main.activity_offline_game.*

class OfflineGameActivity : AppCompatActivity() {

    private lateinit var imgCells: Array<ImageView>

    private lateinit var game: OfflineGame

    private var playerOneScore = 0
    private var playerTwoScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_game)

        imgCells = arrayOf(cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8)

        game = OfflineGame(
            onEndGame = { role ->
                val msg = when(role) {
                    Role.NONE  -> "Ничья"
                    Role.ONE -> "1-й игрок победил"
                    Role.TWO  -> "2-й игрок победил"
                }

                AlertDialog.Builder(this)
                    .setTitle("Игра окончена")
                    .setMessage(msg)
                    .setPositiveButton("Ок") { _, _ -> }
                    .setOnDismissListener {
                        for (cell in imgCells)
                            cell.setImageResource(0)

                        game.restart()

                        if (role == Role.ONE)
                            playerOne_score.text = "${++playerOneScore}"
                        else if (role == Role.TWO)
                            playerTwo_score.text = "${++playerTwoScore}"

                        playerOneTV.alpha = if (game.currentPlayer == Role.ONE) 1f else 0.5f
                        playerTwoTV.alpha = if (game.currentPlayer == Role.TWO) 1f else 0.5f
                    }
                    .create()
                    .show()
            },
            onDraw = { cellId, role ->
                imgCells[cellId].setImageResource(
                    if (role == Cell.CROSS) R.drawable.ic_cross else R.drawable.ic_oval
                )
            }
        )

        reload_btn.setOnClickListener {
            playerOneScore = 0
            playerTwoScore = 0

            playerOne_score.text = "0"
            playerTwo_score.text = "0"

            game.restart()
        }

        for (cellId in imgCells.indices)
            imgCells[cellId].setOnClickListener { playerStep(cellId) }
    }

    private fun playerStep(cellId: Int) {
        game.step(cellId)

        playerOneTV.alpha = if (game.currentPlayer == Role.ONE) 1f else 0.5f
        playerTwoTV.alpha = if (game.currentPlayer == Role.TWO) 1f else 0.5f
    }
}