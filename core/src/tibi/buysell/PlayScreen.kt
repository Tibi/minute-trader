package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.math.vec3
import tibi.buysell.BuySellGame.MyColors.*
import kotlin.math.atan2
import kotlin.math.sqrt


class PlayScreen(val game: BuySellGame) : KtxScreen {

    val model = game.model
    val batch = game.batch
    val bigFont  = game.skin.getFont("big")
    val smallFont  = game.skin.getFont("small")

    val viewport: Viewport = StretchViewport(20f, 400f)  // always 20" and 400 $ visible
    val cam = viewport.camera
    val ui = PlayUI(this, batch)

    var paused = false
    var duration = 60f

    //TODO put in atlas
    val gradient = Texture(Gdx.app.files.internal("gradient.png"))

    override fun show() {
        resize(Gdx.graphics.width, Gdx.graphics.height)
        cam.position.set(-1f, 190f, 0f)
        cam.update()
        paused = false
        Gdx.input.inputProcessor = ui
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
        ui.viewport.update(width, height, true)
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

        letCameraFollowCurve()

        batch.begin()

        drawAxis()

        // Finish line
        val finishX = project(game.lastDuration.minutes * 60f, 0f).x
        batch.color = Color.RED
        batch.draw(gradient, finishX - 100, 0f, 100f, viewport.screenHeight.toFloat())
        bigFont.color = Color.WHITE
        bigFont.draw(batch, "S\nE\nL\nL", finishX - 35, viewport.screenHeight - r(50f))

        ///// Main Curve \\\\\
        model.points.windowed(2).forEach { vals ->
            draw(vals[0].x, vals[0].y, vals[1].x, vals[1].y, true, CURVE.col)
        }

        batch.end()
//        Gdx.app.log("GPU", "# GPU calls: ${batch.renderCalls}")

        ui.act(delta)
        ui.draw()
    }

    private fun letCameraFollowCurve() {
        // Change Y zoom
        val minHeight = model.value * 1.3
        val maxHeight = model.value * 3
        // TODO zoom speed proportional to height - min or max
        val zoomSpeed = 1.005f
        if (cam.viewportHeight < minHeight) cam.viewportHeight *= zoomSpeed
        if (cam.viewportHeight > maxHeight) cam.viewportHeight /= zoomSpeed
        cam.position.y = cam.viewportHeight / 2.2f

        val rightEdge = cam.position.x + cam.viewportWidth / 2
        val diffX = model.time + 3 - rightEdge
        if (diffX > 0) {
            cam.position.x += diffX
        }
        cam.update()
    }


    private fun drawAxis() {

        // start and end in world coords
        val start = unproject(0, viewport.screenHeight)
        val end = unproject(viewport.screenWidth, 0)
        val coarseGridX = 5
        val fineGridX = 1

        // Records the label drawings to be executed later so they show above the lines.
        val textDrawings = mutableListOf<() -> Unit>()

        // Vertical lines
        var x = start.x - start.x % fineGridX
        while (x < end.x) {
            val onCoarseGrid = x.toInt() % coarseGridX == 0
            draw(x, start.y, x, end.y, x == 0f, if (onCoarseGrid) AXIS_MAIN.col else AXIS_LIGHT.col)
            if (onCoarseGrid && x >= 0) {
                // Label
                val p = cam.project(Vector3(x, 0f, 0f))
                val label = formatTime(x)
                textDrawings += { smallFont.draw(batch, label, p.x + r(5), r(20f)) }
            }
            x += fineGridX
        }

        // Horizontal lines
        val coarseGridY = 100
        val fineGridY = 20
        var y = start.y - start.y % fineGridY
        while (y < end.y) {
            val onCoarseGrid = y.toInt() % coarseGridY == 0
            draw(start.x, y, end.x, y, y == 0f, if (onCoarseGrid) AXIS_MAIN.col else AXIS_LIGHT.col)
            if (onCoarseGrid && y >= 0) {
                val p = cam.project(Vector3(0f, y, 0f))
                val label = "${y.toInt()} $"
                textDrawings += { smallFont.draw(batch, label, viewport.screenWidth - 10f, p.y + r(22), 0f, Align.right, false) }
            }
            y += fineGridY
        }

        // Main axis
        draw(0f, 0f, 60f, 0f, true, Color.BLACK)
        draw(0f, 0f, 0f, 1000f, true, Color.BLACK)

        // Bought value line
        if (model.qty > 0) {
            draw(start.x, model.boughtValue, end.x, model.boughtValue, true, RED.col)
        }

        smallFont.color = AXIS_MAIN.col
        textDrawings.forEach { it() }
    }

    private fun formatTime(seconds: Float) =
        "%d'%02d\"".format((seconds / 60).toInt(), (seconds % 60).toInt())


//    val pen = Texture(Gdx.files.internal("pen.png"))
    val penThick = TextureRegion(Texture(Pixmap(3, 3, Pixmap.Format.RGBA8888).apply {
        val lighter = Color.WHITE.cpy()
        lighter.a = .5f
        setColor(lighter)
        fill()
        setColor(Color.WHITE)
        fillRectangle(0, 1, 3, 1)
    }))
    val penFine = TextureRegion(Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
        setColor(Color.WHITE)
        fill()
    }))


    /** Converts x,y from world to screen coordinates. */
    private fun project(x: Float, y: Float): Vector3 = cam.project(vec3(x, y))

    /** Converts x,y from screen to world coordinates. */
    private fun unproject(x: Int, y: Int): Vector3 = cam.unproject(vec3(x.toFloat(), y.toFloat()))

    /** x,y in world coordinates. */
    private fun draw(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
        val start = project(x1, y1).cpy()
        val end = project(x2, y2)
        drawLine(start.x, start.y, end.x, end.y, thick, color)
    }

    /** x,y in screen coordinates. */
    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
        val dx = x2 - x1
        val dy = y2 - y1
        val dist = sqrt(dx * dx + dy * dy)
        val rad = atan2(dy, dx)
        batch.color = color
        if (thick) {
            batch.draw(penThick, x1, y1, 1f, 1f, dist, 3f, 1f, 1f, MathUtils.radiansToDegrees * rad)
        } else {
            batch.draw(penFine,  x1, y1, 0f, 0f, dist, 1f, 1f, 1f, MathUtils.radiansToDegrees * rad)
        }
    }

    override fun dispose() {
        batch.dispose()  //TODO not if owned by game
        ui.dispose()
    }
}