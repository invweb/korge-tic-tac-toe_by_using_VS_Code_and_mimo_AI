package logic

fun checkWinBoard(board: Array<Array<String>>, player: String): Boolean {
    val n = board.size
    for (i in 0 until n) {
        if (board[i].all { it == player }) return true
        if (board.all { it[i] == player }) return true
    }
    if ((0 until n).all { board[it][it] == player }) return true
    if ((0 until n).all { board[it][n - 1 - it] == player }) return true
    return false
}

fun minimax(board: Array<Array<String>>, depth: Int, isMax: Boolean, aiMark: String, humanMark: String, alpha: Int, beta: Int): Int {
    if (checkWinBoard(board, aiMark)) return 10 - depth
    if (checkWinBoard(board, humanMark)) return depth - 10
    if (board.all { row -> row.all { it.isNotEmpty() } }) return 0

    if (isMax) {
        var best = -100
        var a = alpha
        for (r in board.indices) for (c in board[r].indices) {
            if (board[r][c].isEmpty()) {
                board[r][c] = aiMark
                best = maxOf(best, minimax(board, depth + 1, false, aiMark, humanMark, a, beta))
                board[r][c] = ""
                a = maxOf(a, best)
                if (a >= beta) break
            }
        }
        return best
    } else {
        var best = 100
        var b = beta
        for (r in board.indices) for (c in board[r].indices) {
            if (board[r][c].isEmpty()) {
                board[r][c] = humanMark
                best = minOf(best, minimax(board, depth + 1, true, aiMark, humanMark, alpha, b))
                board[r][c] = ""
                b = minOf(b, best)
                if (alpha >= b) break
            }
        }
        return best
    }
}

fun findBestMove(board: Array<Array<String>>, aiMark: String): Pair<Int, Int>? {
    val humanMark = if (aiMark == "X") "O" else "X"
    var bestScore = -100
    var bestMove: Pair<Int, Int>? = null
    var alpha = -100
    for (r in board.indices) for (c in board[r].indices) {
        if (board[r][c].isEmpty()) {
            board[r][c] = aiMark
            val s = minimax(board, 0, false, aiMark, humanMark, alpha, 100)
            board[r][c] = ""
            if (s > bestScore) { bestScore = s; bestMove = Pair(r, c) }
            alpha = maxOf(alpha, bestScore)
        }
    }
    return bestMove
}
