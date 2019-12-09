package tibi.buysell

import tibi.buysell.Leaderboard.Score
import java.net.URL
import java.text.SimpleDateFormat

class DreamLoLeaderboard(val key: String) : Leaderboard {

    override fun get(name: String): Score = send("pipe-get/$name")[0]

    override fun get(top: Int): List<Score> = send("pipe/$top")

    override fun update(score: Score): Score = send("add-pipe/${score.name}/${score.score}")[0]

    override fun delete(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // TODO send request on a separate thread, wait for it, send result.
    private fun send(cmd: String): List<Score> = URL("http://dreamlo.com/lb/$key/$cmd")
        .readText().lines().mapNotNull(this::score)

    val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
    private fun score(line: String): Score? {
        val elems = line.split("|")
        return if (elems.size > 4) Score(elems[0], elems[1].toInt(), dateFormat.parse(elems[4]))
               else null
    }
}

