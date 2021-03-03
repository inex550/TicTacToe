package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.tictactoe.gameClasses.Cell
import com.example.tictactoe.gameClasses.Difficulty
import com.example.tictactoe.gameClasses.GameVsBot
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var imgCells: Array<ImageView>

    private var playerScore: Int = 0
    private var botScore: Int = 0

    private lateinit var game: GameVsBot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val difficulty = intent.getSerializableExtra("difficulty") as Difficulty

        difficultyTextView.text = when (difficulty) {
            Difficulty.EASY -> "Легко"
            Difficulty.NORMAL -> "Средне"
            Difficulty.HARD -> "Невозможно"
        }

        imgCells = arrayOf(cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8)

        game = GameVsBot(
            difficulty,
            { role ->
                val msg = when(role) {
                    Cell.NONE -> "Ничья"
                    Cell.OVAL -> "Бот победил"
                    Cell.CROSS -> "Игрок победил"
                }

                AlertDialog.Builder(this)
                    .setTitle("Игра окончена")
                    .setMessage(msg)
                    .setPositiveButton("Ок") { _, _ -> }
                    .setOnDismissListener {
                        for (cell in imgCells)
                            cell.setImageResource(0)

                        game.clear()

                        if (role == Cell.OVAL)
                            bot_score.text = "${++botScore}"
                        else if (role == Cell.CROSS)
                            player_score.text = "${++playerScore}"
                    }
                    .create()
                    .show()
            },
            { cell_id, role ->
                if (role == Cell.CROSS)
                    imgCells[cell_id].setImageResource(R.drawable.ic_cross)

                else if (role == Cell.OVAL)
                    imgCells[cell_id].setImageResource(R.drawable.ic_oval)
            }
        )

        for (i in imgCells.indices)
            imgCells[i].setOnClickListener { playerStep(i) }

        reload_btn.setOnClickListener {
            playerScore = 0
            botScore = 0

            player_score.text = "0"
            bot_score.text = "0"

            for (cell in imgCells)
                cell.setImageResource(0)

            game.clear()
        }
    }

    private fun playerStep(cell_id: Int) {
        game.playerStep(cell_id)
    }
}