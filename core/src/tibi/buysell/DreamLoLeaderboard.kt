package tibi.buysell

import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.httpRequest
import tibi.buysell.Leaderboard.Score
import java.text.SimpleDateFormat

class DreamLoLeaderboard(val key: String) : Leaderboard {

    override fun get(name: String, processResult: (List<Score>) -> Unit) {
        send("pipe-get/$name", processResult)
    }

    override fun get(top: Int, processResult: (List<Score>) -> Unit) {
        send("pipe/$top", processResult)
    }

    override fun update(score: Score, processResult: (List<Score>) -> Unit) = send(
        "add-pipe/${score.name}/${score.score}",
        processResult
    )

    override fun delete(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Sends request on a separate thread, calls processResult with the response */
    private fun send(cmd: String, processResult: (List<Score>) -> Unit) {
        KtxAsync.launch {
            val response = httpRequest("http://dreamlo.com/lb/$key/$cmd")
            processResult(response.contentAsString.lines().mapNotNull { readScore(it) })
        }
    }

    val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
    private fun readScore(line: String): Score? {
        val elems = line.split("|")
        return if (elems.size > 4) Score(elems[0], elems[1].toInt(), dateFormat.parse(elems[4]))
               else null
    }

    fun http() {
        KtxAsync.launch {
            val response = httpRequest(url = "https://example.com")
            println("""Reading response on ${Thread.currentThread()}. Website content: ${response.contentAsString}""")
        }
    }
}

