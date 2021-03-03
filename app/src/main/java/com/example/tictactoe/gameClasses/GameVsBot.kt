package com.example.tictactoe.gameClasses

import kotlin.random.Random

class GameVsBot(
    difficulty: Difficulty,

    val onEndGame: (Cell) -> Unit,
    val onDraw: (Int, Cell) -> Unit
) {
    private val cells = Array(9) { Cell.NONE }

    var aiStep: () -> Unit = when (difficulty) {
        Difficulty.EASY -> { -> aiStepEasy() }
        Difficulty.NORMAL -> { -> aiStepNormal() }
        Difficulty.HARD -> { -> aiStepHard() }
    }

    private fun aiStepEasy() {
        while (true) {
            val randCell = Random.nextInt(0, 9)
            if (cells[randCell] == Cell.NONE) {
                draw(randCell, Cell.OVAL)
                break
            }
        }
    }

    private fun aiStepNormal() {
        val aiWinWay = checkWinWay(Cell.OVAL)
        if (aiWinWay != -1) {
            draw(aiWinWay, Cell.OVAL)
            onEndGame(Cell.OVAL)
            return
        }

        val playerWinWay = checkWinWay(Cell.CROSS)
        if (playerWinWay != -1) {
            draw(playerWinWay, Cell.OVAL)
            return
        }

        while (true) {
            val randCell = Random.nextInt(0, 9)
            if (cells[randCell] == Cell.NONE) {
                draw(randCell, Cell.OVAL)
                break
            }
        }
    }

    private var stage = 0

    private fun aiStepHard() {
        var aiCell = -1

        if (stage == 1)
            aiCell = if (cells[4] != Cell.CROSS) 4 else 0

        else if (stage == 2) {
            val playerWinWay = checkWinWay(Cell.CROSS)
            if (playerWinWay != -1)
                aiCell = playerWinWay
            else if (cells[4] == Cell.OVAL) {
                if (cells[0] == Cell.CROSS && cells[8] == Cell.CROSS) aiCell = 3
                else if (cells[2] == Cell.CROSS && cells[6] == Cell.CROSS) aiCell = 1
                else if (cells[0] == Cell.CROSS && cells[5] == Cell.CROSS) aiCell = 2
                else if (cells[0] == Cell.CROSS && cells[7] == Cell.CROSS) aiCell = 6
                else if (cells[2] == Cell.CROSS && cells[3] == Cell.CROSS) aiCell = 0
                else if (cells[2] == Cell.CROSS && cells[7] == Cell.CROSS) aiCell = 8
                else if (cells[1] == Cell.CROSS && cells[6] == Cell.CROSS) aiCell = 0
                else if (cells[6] == Cell.CROSS && cells[5] == Cell.CROSS) aiCell = 8
                else if (cells[1] == Cell.CROSS && cells[8] == Cell.CROSS) aiCell = 0
                else if (cells[3] == Cell.CROSS && cells[8] == Cell.CROSS) aiCell = 6
                else if (cells[1] == Cell.CROSS && cells[3] == Cell.CROSS) aiCell = 0
                else if (cells[1] == Cell.CROSS && cells[5] == Cell.CROSS) aiCell = 2
                else if (cells[3] == Cell.CROSS && cells[7] == Cell.CROSS) aiCell = 6
                else if (cells[5] == Cell.CROSS && cells[7] == Cell.CROSS) aiCell = 8
            } else aiCell = 6
        } else {
            val aiWinWay = checkWinWay(Cell.OVAL)
            if (aiWinWay != -1) aiCell = aiWinWay
            else {
                val playerWinWay = checkWinWay(Cell.CROSS)
                if (playerWinWay != -1) aiCell = playerWinWay
            }
        }

        if (aiCell != -1) draw(aiCell, Cell.OVAL)
        else while (true) {
            val randCell = Random.nextInt(0, 9)
            if (cells[randCell] == Cell.NONE) {
                draw(randCell, Cell.OVAL)
                break
            }
        }

        if (checkWin(Cell.OVAL)) onEndGame(Cell.OVAL)
    }

    fun playerStep(cell_id: Int) {
        if (cells[cell_id] != Cell.NONE) return

        draw(cell_id, Cell.CROSS)

        when {
            checkWin(Cell.CROSS) -> onEndGame(Cell.CROSS)
            checkEnd() -> onEndGame(Cell.NONE)
            else -> {
                stage += 1
                aiStep()
            }
        }
    }

    private fun draw(cell_id: Int, role: Cell) {
        cells[cell_id] = role
        onDraw(cell_id, role)
    }

    private fun checkEnd(): Boolean {
        for (cell in cells) {
            if (cell == Cell.NONE)
                return false
        }

        return true
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

    private fun checkWinWay(role: Cell): Int {
        if (cells[0] == role && cells[1] == role && cells[2] == Cell.NONE) return 2
        if (cells[3] == role && cells[4] == role && cells[5] == Cell.NONE) return 5
        if (cells[6] == role && cells[7] == role && cells[8] == Cell.NONE) return 8
        if (cells[1] == role && cells[2] == role && cells[0] == Cell.NONE) return 0
        if (cells[4] == role && cells[5] == role && cells[3] == Cell.NONE) return 3
        if (cells[7] == role && cells[8] == role && cells[6] == Cell.NONE) return 6
        if (cells[0] == role && cells[3] == role && cells[6] == Cell.NONE) return 6
        if (cells[1] == role && cells[4] == role && cells[7] == Cell.NONE) return 7
        if (cells[2] == role && cells[5] == role && cells[8] == Cell.NONE) return 8
        if (cells[3] == role && cells[6] == role && cells[0] == Cell.NONE) return 0
        if (cells[4] == role && cells[7] == role && cells[1] == Cell.NONE) return 1
        if (cells[5] == role && cells[8] == role && cells[2] == Cell.NONE) return 2
        if (cells[0] == role && cells[4] == role && cells[8] == Cell.NONE) return 8
        if (cells[4] == role && cells[8] == role && cells[0] == Cell.NONE) return 0
        if (cells[2] == role && cells[4] == role && cells[6] == Cell.NONE) return 6
        if (cells[4] == role && cells[6] == role && cells[2] == Cell.NONE) return 2
        if (cells[0] == role && cells[2] == role && cells[1] == Cell.NONE) return 1
        if (cells[3] == role && cells[5] == role && cells[4] == Cell.NONE) return 4
        if (cells[6] == role && cells[8] == role && cells[7] == Cell.NONE) return 7
        if (cells[0] == role && cells[6] == role && cells[3] == Cell.NONE) return 3
        if (cells[1] == role && cells[7] == role && cells[4] == Cell.NONE) return 4
        if (cells[2] == role && cells[8] == role && cells[5] == Cell.NONE) return 5
        if (cells[0] == role && cells[8] == role && cells[4] == Cell.NONE) return 4
        if (cells[2] == role && cells[6] == role && cells[4] == Cell.NONE) return 4
        return -1
    }

    fun clear() {
        for (i in cells.indices)
            cells[i] = Cell.NONE

        stage = 0
    }
}