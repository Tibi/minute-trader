package tibi.buysell

import java.util.*

/**
 * Interface to an online leaderboard.
 */
interface Leaderboard {

    data class Score(val name: String, val score: Int, val date: Date)

    fun get(name: String): Score
    fun get(top: Int): List<Score>
    /** Returns new high score */
    fun update(score: Score): Score
    fun delete(name: String)
    fun clearAll()

}