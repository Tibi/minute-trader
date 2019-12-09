package tibi.buysell

import ktx.scene2d.label
import ktx.scene2d.table

class ScoreScreen(game: BuySellGame) : UiScreen(game) {

    override fun show() {
        super.show()
        stage.addActor(table {
            setFillParent(true)
            label("High Scores", "big")  // TODO I18n
            add().height(100f).row()  // TODO there must be a simpler way to leave space!
            for (score in game.leaderboard.get(5)) {
                row()
                label("${score.name} ${score.score}")
            }
        })
    }
}
