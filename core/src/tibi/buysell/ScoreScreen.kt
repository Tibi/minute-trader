package tibi.buysell

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.scene2d.label
import ktx.scene2d.table

class ScoreScreen(game: BuySellGame) : UiScreen(game) {

    val scoreLabels = List(5) { Label("", game.skin) }

    init {
        stage.addActor(table {
            setFillParent(true)
            label("High Scores", "big")  // TODO I18n
            add().height(100f).row()  // TODO there must be a simpler way to leave space!
            scoreLabels.forEach { add(it).row() }
        })
    }

    override fun show() {
        super.show()
        scoreLabels.forEach { it.setText("") }
        game.leaderboard.get(5) { scores ->
            for ((i, score) in scores.withIndex()) {
                scoreLabels[i].setText("${score.name} ${score.score}")
            }
        }
    }
}
