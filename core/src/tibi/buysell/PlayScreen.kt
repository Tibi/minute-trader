package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen


class PlayScreen(val game: BuySellGame) : KtxScreen {

    val model = game.model

    val batch = SpriteBatch()
    val shape = ShapeRenderer()
    val font  = BitmapFont()
    val viewport: Viewport = ScreenViewport()
    val cam = viewport.camera
    val ui = MainUI(this, game.skin)

    private var width = 0f
    private var height = 0f

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
                    else -> return false
                }
                return true
            }
        }
        Gdx.input.inputProcessor = InputMultiplexer(ui, keyProcessor)
    }

    override fun resize(newWidth: Int, newHeight: Int) {
        viewport.update(newWidth, newHeight, false)
        width = newWidth.toFloat()
        height = newHeight.toFloat()
        batch.projectionMatrix = Matrix4().apply { setToOrtho2D(0f, 0f, width, height) }
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(.9f, .95f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (!paused) model.update(delta)
        if (model.points.size < 2) return

        duration -= delta
        if (duration <= 0) {
            game.gameFinished()
            return
        }

        // Let the camera follow the curve
        val diffX = model.time * scaleX - (cam.position.x + cam.viewportWidth / 2 - 50)
        if (diffX > 0) {
            if (diffX > 50) {
                cam.position.x += diffX
            } else {
                // smooth scrolling
                cam.position.x++
            }
        }
        val y = model.value
        if (y + 50 > cam.position.y + cam.viewportHeight / 2) {
            cam.position.y++
        } else if (y - 50 < cam.position.y - cam.viewportHeight / 2) {
            cam.position.y--
        }
        cam.update()

        shape.projectionMatrix = cam.combined
        shape.begin(ShapeType.Line)
        drawAxis()

        shape.setColor(.1f, .1f, .5f, 1f)
        model.points.windowed(2).forEach { vals ->
            draw(vals[0].x * scaleX, vals[0].y, vals[1].x * scaleX, vals[1].y, true, blue)
        }
        shape.end()

        Gdx.gl.glEnable(GL20.GL_BLEND)
        // Draw a text background
        shape.begin(ShapeType.Filled)
        shape.color = txtBgCol
        val p1 = cam.unproject(Vector3(10f, 100f, 0f))
        shape.rect(p1.x, p1.y, 160f, 80f)
        shape.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)

        batch.begin()
        val xText = 100f
        font.color = blue
        font.draw(batch, "${model.qty}", xText, height - 30, 0f, Align.right, false)
        font.draw(batch, "Owned", xText+10, height - 30)
        font.draw(batch, "%,d $".format(model.moneyLeft.toInt()), xText, height - 70, 0f, Align.right, false)
        font.draw(batch, "Left", xText+10, height - 70)
        batch.end()

        ui.act(delta)
        ui.draw()
    }

    val blue = Color(.31f, .31f, 1f, 1f)
    val blueLight = Color(.7f, .8f, 1f, 1f)
    val red = Color.valueOf("FF6666")
    val txtBgCol = Color.valueOf("#ceff9d88")

    private fun drawAxis() {

        font.color = blueLight

        // World coordinates of the screen viewport
        val start = cam.unproject(Vector3(0f, height, 0f))
        val end = cam.unproject(Vector3(width, 0f, 0f))
        val coarseGrid = 100
        val fineGrid = 25

        batch.begin()

        // Vertical lines
        var x = start.x - start.x % fineGrid
        while (x < end.x) {
            val onCoarseGrid = x.toInt() % coarseGrid == 0
            draw(x, start.y, x, end.y, x == 0f, if (onCoarseGrid) blue else blueLight)
            if (onCoarseGrid) {
                // Label
                val p = cam.project(Vector3(x, 0f, 0f))
                font.draw(batch, formatTime(x / scaleX), p.x + 5, 20f)
            }
            x += fineGrid
        }

        // Horizontal lines
        var y = start.y - start.y % fineGrid
        while (y < end.y) {
            val onCoarseGrid = y.toInt() % coarseGrid == 0
            draw(start.x, y, end.x, y, y == 0f, if (onCoarseGrid) blue else blueLight)
            if (onCoarseGrid) {
                // Label
                val p = cam.project(Vector3(0f, y, 0f))
                font.draw(batch, y.toInt().toString(), width - 10, p.y + 16, 0f, Align.right, false)
            }
            y += fineGrid
        }
        batch.end()

        // Bought value line
        if (model.qty > 0) {
            draw(start.x, model.boughtValue, end.x, model.boughtValue, true, red)
        }

        // Finish line
        val finishX = game.lastDuration.minutes * 60f * scaleX
        draw(finishX, start.y, finishX, end.y, true, red)
    }

    private fun formatTime(seconds: Float) =
        "%d:%02d".format((seconds / 60).toInt(), (seconds % 60).toInt())

    fun draw(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
        shape.color = color
        shape.line(x1, y1, x2, y2)
        if (thick) {
            shape.line(x1+1, y1+1, x2+1, y2+1)
        }
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shape.dispose()
        ui.dispose()
    }
}