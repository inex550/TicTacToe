package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tictactoe.gameClasses.Difficulty
import kotlinx.android.synthetic.main.activity_game_choice.*

class GameChoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_choice)

        easyBtn.setOnClickListener {
            startBotGame(Difficulty.EASY)
        }

        normalBtn.setOnClickListener {
            startBotGame(Difficulty.NORMAL)
        }

        hardBtn.setOnClickListener {
            startBotGame(Difficulty.HARD)
        }

        offlineGameBtn.setOnClickListener {
            val intent = Intent(this, OfflineGameActivity::class.java)
            startActivity(intent)
        }

        onlineGameBtn.setOnClickListener {
            val intent = Intent(this, RoomsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startBotGame(difficulty: Difficulty) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("difficulty", difficulty)
        startActivity(intent)
    }
}