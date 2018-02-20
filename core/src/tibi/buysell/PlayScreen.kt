package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.app.clearScreen
import tibi.buysell.BuySellGame.MyColors.*


class PlayScreen(val game: BuySellGame) : KtxScreen {

    val model = game.model

    val batch = SpriteBatch()// game.batch
    val shape = ShapeRenderer()
    val bigFont  = game.skin.getFont("big")
    val smallFont  = game.skin.getFont("small")
    val viewport: Viewport = ScreenViewport()
    val cam = viewport.camera
    val ui = PlayUI(game, batch)

    var screenWidth = 0f
    var screenHeight = 0f

    /** In pixels / second */
    var scaleX = 40

    var paused = false
    var duration = 60f

    override fun show() {
        resize(Gdx.graphics.width, Gdx.graphics.height)
        cam.position.set(220f, 160f, 0f)
        cam.update()

        val keyProcessor = object : InputAdapter() {
            override fun keyDown(key: Int): Boolean {
                when (key) {
                    Input.Keys.B -> model.buy()
                    Input.Keys.S -> model.sell()
                    Input.Keys.P -> paused = paused.not()
                    Input.Keys.ESCAPE -> game.setScreen<MenuScreen>()
                    else -> return false
                }
                return true
            }
        }
        Gdx.input.inputProcessor = InputMultiplexer(ui, keyProcessor)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
        ui.viewport.update(width, height, true)
        screenWidth = width.toFloat()
        screenHeight = height.toFloat()
        batch.projectionMatrix = Matrix4().apply { setToOrtho2D(0f, 0f, screenWidth, screenHeight) }
    }

    override fun render(delta: Float) {

        clearScreen(BG.col.r, BG.col.g, BG.col.b)
        if (!paused) {
            model.update(delta)
            if (model.points.size < 2) return
            duration -= delta
            if (duration <= 0) {
                game.gameFinished()
                return
            }
        }

        // Let the camera follow the curve
        val rightEdge = cam.position.x + cam.viewportWidth / 2
        val diffX = model.time * scaleX + 100 - rightEdge
        if (diffX > 0) {
            cam.position.x += diffX
        }
        val topEdge = cam.position.y + cam.viewportHeight / 2
        val diffYTop = model.value + 50 - topEdge
        if (diffYTop > 0) {
            cam.position.y += diffYTop
        } else {
            val bottomEdge = cam.position.y - cam.viewportHeight / 2
            val diffYBottom = bottomEdge - (model.value - 50)
            if (diffYBottom > 30) {
                cam.position.y -= diffYBottom
            } else if (diffYBottom > 0) {
                cam.position.y--
            }
        }
        cam.update()

        shape.projectionMatrix = cam.combined

        // Axis
        shape.begin(ShapeType.Line)
        drawAxis()
        shape.end()

        // Start filled shapes
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.begin(ShapeType.Filled)

        // Draw a text background
        shape.color = TEXT_BG.col
        val p1 = cam.unproject(Vector3(10f, r(130f), 0f))
        shape.rect(p1.x, p1.y, r(340f), r(110f))

        // Finish line
        val start = cam.unproject(Vector3(0f, screenHeight, 0f))
        val finishX = game.lastDuration.minutes * 60f * scaleX
        shape.rect(finishX - 300, start.y, 300f, screenHeight,
                // bottom left   bottom right    top right       top left
                TRANSPARENT.col, RED_BUTTON.col, RED_BUTTON.col, TRANSPARENT.col)

        // End filled shapes
        shape.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)

        ///// Main Curve \\\\\
        shape.begin(ShapeType.Line)
        shape.color = CURVE.col
        model.points.windowed(2).forEach { vals ->
            draw(vals[0].x * scaleX, vals[0].y, vals[1].x * scaleX, vals[1].y, true, CURVE.col)
        }
        shape.end()

        // Balance Text
        val xText = r(180f)
        bigFont.color = CURVE.col
        batch.begin()
        bigFont.draw(batch, "${model.qty}", xText, screenHeight - r(30), 0f, Align.right, false)
        bigFont.draw(batch, "Owned", xText + r(20), screenHeight - r(30))
        bigFont.draw(batch, "%,d $".format(model.moneyLeft.toInt()), xText, screenHeight - r(90), 0f, Align.right, false)
        bigFont.draw(batch, "Left", xText + r(20), screenHeight - r(90))
        batch.end()

        ui.act(delta)
        ui.draw()
    }

    private fun drawAxis() {

        smallFont.color = AXIS_MAIN.col

        // World coordinates of the screen viewport
        val start = cam.unproject(Vector3(0f, screenHeight, 0f))
        val end = cam.unproject(Vector3(screenWidth, 0f, 0f))
        val coarseGrid = 100
        val fineGrid = 25

        batch.begin()

        // Vertical lines
        var x = start.x - start.x % fineGrid
        while (x < end.x) {
            val onCoarseGrid = x.toInt() % coarseGrid == 0
            draw(x, start.y, x, end.y, x == 0f, if (onCoarseGrid) AXIS_MAIN.col else AXIS_LIGHT.col)
            if (onCoarseGrid && x >= 0) {
                // Label
                val p = cam.project(Vector3(x, 0f, 0f))
                smallFont.draw(batch, formatTime(x / scaleX), p.x + 5, 20f)
            }
            x += fineGrid
        }

        // Horizontal lines
        var y = start.y - start.y % fineGrid
        while (y < end.y) {
            val onCoarseGrid = y.toInt() % coarseGrid == 0
            draw(start.x, y, end.x, y, y == 0f, if (onCoarseGrid) AXIS_MAIN.col else AXIS_LIGHT.col)
            if (onCoarseGrid && y >= 0) {
                // Label
                val p = cam.project(Vector3(0f, y, 0f))
                smallFont.draw(batch, "${y.toInt()} $", screenWidth - 10, p.y + 16, 0f, Align.right, false)
            }
            y += fineGrid
        }
        batch.end()

        // Bought value line
        if (model.qty > 0) {
            draw(start.x, model.boughtValue, end.x, model.boughtValue, true, RED.col)
        }
    }

    private fun formatTime(seconds: Float) =
        "%d'%02d\"".format((seconds / 60).toInt(), (seconds % 60).toInt())

    fun draw(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
        shape.color = color
        shape.line(x1, y1, x2, y2)
        if (thick) {
            shape.line(x1+1, y1+1, x2+1, y2+1)
        }
    }

    override fun dispose() {
        batch.dispose()  //TODO not if owned by game
        shape.dispose()
        ui.dispose()
    }
}