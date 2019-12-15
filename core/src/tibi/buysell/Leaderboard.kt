package tibi.buysell

import java.util.*

/**
 * Interface to an online leaderboard.
 */
interface Leaderboard {

    data class Score(val name: String, val score: Int, val date: Date)

    fun get(name: String, processResult: (List<Score>) -> Unit)
    fun get(top: Int, processResult: (List<Score>) -> Unit)
    /** Returns new high score */
    fun update(score: Score, processResult: (List<Score>) -> Unit)
    fun delete(name: String)
    fun clearAll()

}