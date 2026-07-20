package logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameLogicTest {

    private fun board(vararg rows: String): Array<Array<String>> {
        return rows.map { row ->
            row.map { if (it == '.') "" else it.toString() }.toTypedArray()
        }.toTypedArray()
    }

    @Test
    fun testCheckWinRow() {
        val b = board("XXX", "...", "...")
        assertTrue(checkWinBoard(b, "X"))
    }

    @Test
    fun testCheckWinColumn() {
        val b = board("X..", "X..", "X..")
        assertTrue(checkWinBoard(b, "X"))
    }

    @Test
    fun testCheckWinDiagonal() {
        val b = board("X..", ".X.", "..X")
        assertTrue(checkWinBoard(b, "X"))
    }

    @Test
    fun testCheckWinAntiDiagonal() {
        val b = board("..X", ".X.", "X..")
        assertTrue(checkWinBoard(b, "X"))
    }

    @Test
    fun testCheckWinMiddleRow() {
        val b = board("...", "OOO", "...")
        assertTrue(checkWinBoard(b, "O"))
    }

    @Test
    fun testCheckWinMiddleColumn() {
        val b = board(".O.", ".O.", ".O.")
        assertTrue(checkWinBoard(b, "O"))
    }

    @Test
    fun testNoWinner() {
        val b = board("XO.", "XO.", "...")
        val result = findWinLine(b, "X")
        assertNull(result)
    }

    @Test
    fun testFindWinLineRow() {
        val b = board("XXX", "...", "...")
        val line = findWinLine(b, "X")
        assertNotNull(line)
        assertEquals(3, line.size)
        assertEquals(Pair(0, 0), line[0])
        assertEquals(Pair(0, 1), line[1])
        assertEquals(Pair(0, 2), line[2])
    }

    @Test
    fun testFindWinLineColumn() {
        val b = board("O..", "O..", "O..")
        val line = findWinLine(b, "O")
        assertNotNull(line)
        assertEquals(Pair(0, 0), line[0])
        assertEquals(Pair(1, 0), line[1])
        assertEquals(Pair(2, 0), line[2])
    }

    @Test
    fun testFindWinLineDiagonal() {
        val b = board("X..", ".X.", "..X")
        val line = findWinLine(b, "X")
        assertNotNull(line)
        assertEquals(Pair(0, 0), line[0])
        assertEquals(Pair(1, 1), line[1])
        assertEquals(Pair(2, 2), line[2])
    }

    @Test
    fun testMinimaxWinningMove() {
        val b = board("X.X", "...", "O.O")
        val move = findBestMove(b, "O", 2)
        assertNotNull(move)
        val (r, c) = move
        b[r][c] = "O"
        assertTrue(checkWinBoard(b, "O"))
    }

    @Test
    fun testMinimaxBlocksOpponent() {
        val b = board("X.X", "...", "...")
        val move = findBestMove(b, "O", 2)
        assertNotNull(move)
        b[move.first][move.second] = "O"
        assertNull(findWinLine(b, "X"))
    }

    @Test
    fun testFindBestMoveDifficultyEasy() {
        val b = board("...", "...", "...")
        val move = findBestMove(b, "X", 0)
        assertNotNull(move)
    }

    @Test
    fun testDrawBoard() {
        val b = board("XOX", "OXO", "XOX")
        assertTrue(b.all { row -> row.all { it.isNotEmpty() } })
    }
}
