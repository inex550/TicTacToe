package com.example.tictactoe.gameClasses

class OfflineGame(
    val onEndGame: (Role) -> Unit,
    val onDraw: (Int, Cell) -> Unit
) {
    var currentFigure: Cell = Cell.CROSS
    var currentPlayer: Role = Role.ONE

    private val cells: Array<Cell> = Array(9) {Cell.NONE}

    fun step(cellId: Int) {
        if (cells[cellId] != Cell.NONE) return

        cells[cellId] = currentFigure
        onDraw(cellId, currentFigure)

        when {
            checkWin(currentFigure) -> onEndGame(currentPlayer)
            checkEnd() -> onEndGame(Role.NONE)
            else -> {
                currentFigure = if (currentFigure == Cell.CROSS) Cell.OVAL else Cell.CROSS
                currentPlayer = if (currentPlayer == Role.ONE) Role.TWO else Role.ONE
            }
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

    fun restart() {
        for (cellId in cells.indices)
            cells[cellId] = Cell.NONE

        currentFigure = Cell.CROSS
        currentPlayer = if (currentPlayer == Role.ONE) Role.TWO else Role.ONE
    }
}