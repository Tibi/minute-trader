package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.i18n.get
import ktx.math.vec3
import tibi.buysell.BuySellGame.MyColors.*
import kotlin.math.*


class PlayScreen(val game: BuySellGame) : KtxScreen {

    val model = game.model
    val batch = game.batch
    val txt = game.txt
    val bigFont  = game.skin.getFont("big")
    val smallFont  = game.skin.getFont("small")

    val viewport = StretchViewport(20f, 400f)  // 20" and 400 $ visible
    val cam = viewport.camera
    val ui = PlayUI(this, batch)

    var paused = false
    var duration = 60f

    val screenWidth get() = viewport.screenWidth.toFloat()
    val screenHeight get() = viewport.screenHeight.toFloat()

    val gradient = game.skin.atlas.findRegion("gradient")
    val penThick = game.skin.atlas.findRegion("penThick")
    val penFine  = game.skin.atlas.findRegion("penFine")
    val buyMarker = game.skin.atlas.findRegion("buy-marker")
    val sellMarker = game.skin.atlas.findRegion("sell-marker")

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
        val deltaTime = min(0.3f, delta)
        clearScreen(BG.col.r, BG.col.g, BG.col.b)
        if (!paused) {
            model.update(deltaTime)
            if (model.points.size < 2) return
            duration -= deltaTime
            if (duration <= 0) {
                ui.gameOver()
            }
        }

        letCameraFollowCurve()

        batch.begin()

        // Buy & Sell backgrounds below and above by line
        if (model.qty > 0) {
            val boughtY = project(0f, model.boughtValue).y
            batch.color = RED_BG.col
            batch.draw(penFine, 0f, 0f, screenWidth, boughtY)
            batch.color = GREEN_BG.col
            batch.draw(penFine, 0f, boughtY, screenWidth, screenHeight)
        }

        drawAxis()

        // Finish line
        val finishX = project(game.lastDuration.minutes * 60f, 0f).x
        batch.color = GREEN_BUTTON.col
        batch.draw(gradient, finishX - 100, 0f, 100f, screenHeight)
        bigFont.color = WHITE.col
        bigFont.draw(batch, txt["sellFinish"], finishX - 46, screenHeight - r(50f), 36f, Align.center, true)

        // Bought value line
        if (model.qty > 0) {
            val boughtY = project(0f, model.boughtValue).y
            drawLineScreen(0f, boughtY, screenWidth, boughtY, true, RED.col)
        }

        ///// Main Curve \\\\\
        model.points.windowed(2).forEach { vals ->
            drawLine(vals[0].x, vals[0].y, vals[1].x, vals[1].y, true, CURVE.col)
        }

        model.buys .forEach { drawMarker(it, buy = true ) }
        model.sells.forEach { drawMarker(it, buy = false) }

        batch.end()
        Gdx.app.log("GPU", "# GPU calls: ${batch.renderCalls}")

        ui.act(deltaTime)
        ui.draw()
    }

    private fun letCameraFollowCurve() {
        // Change Y zoom
        val minHeight = model.value * 1.3f
        val maxHeight = model.value * 3f
        if (cam.viewportHeight < minHeight) {
            // zoom up speed proportional to height - min
            val zoomSpeed = 1 + (minHeight - cam.viewportHeight) / cam.viewportHeight / 10
            cam.viewportHeight *= zoomSpeed
        }
        if (cam.viewportHeight > maxHeight) {
            val zoomSpeed = 1 + (cam.viewportHeight - maxHeight) / cam.viewportHeight / 20
            cam.viewportHeight /= zoomSpeed
        }
        cam.position.y = cam.viewportHeight / 2.2f

        // Scroll to the right
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
            drawLine(x, start.y, x, end.y, x == 0f, if (onCoarseGrid) AXIS_MAIN.col else AXIS_LIGHT.col)
            if (onCoarseGrid && x >= 0) {
                // Label
                val p = cam.project(Vector3(x, 0f, 0f))
                val label = formatTime(x)
                textDrawings += { smallFont.draw(batch, label, p.x + r(5), r(20f)) }
            }
            x += fineGridX
        }

        // Horizontal lines
        val digits = log10(cam.viewportHeight / 5f).roundToInt()
        val coarseGridY = 10f.pow(digits)
        val fineGridY = coarseGridY / 5
        var y = 0f
        var fineCount = 0
        while (y < end.y) {
            val onCoarseGrid = fineCount == 0
            drawLine(start.x, y, end.x, y, false, if (onCoarseGrid) AXIS_MAIN.col else AXIS_LIGHT.col)
            if (onCoarseGrid) {
                val p = cam.project(Vector3(0f, y, 0f))
                val label = txt["amount", "%,.${max(0, -digits)}f".format(y)]
                textDrawings += { smallFont.draw(batch, label, viewport.screenWidth - 10f, p.y + r(22), 0f, Align.right, false) }
            }
            y += fineGridY
            fineCount++
            if (fineCount == 5) fineCount = 0
        }
        // Main axis
        drawLine(0f, 0f, 60f, 0f, true, DARK_TEXT.col)
        drawLine(0f, 0f, 0f, 100_000f, true, DARK_TEXT.col)

        smallFont.color = AXIS_MAIN.col
        textDrawings.forEach { it() }
    }

    private fun formatTime(seconds: Float) =
        "%d'%02d\"".format((seconds / 60).toInt(), (seconds % 60).toInt())

    /** Converts x,y from world to screen coordinates. */
    private fun project(x: Float, y: Float): Vector3 = cam.project(vec3(x, y))

    /** Converts x,y from screen to world coordinates. */
    private fun unproject(x: Int, y: Int): Vector3 = cam.unproject(vec3(x.toFloat(), y.toFloat()))

    /** x,y in world coordinates. */
    private fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
        val start = project(x1, y1)
        val end = project(x2, y2)
        drawLineScreen(start.x, start.y, end.x, end.y, thick, color)
    }

    /** x,y in screen coordinates. */
    fun drawLineScreen(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
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

    fun drawMarker(pos: Vector2, buy: Boolean) {
        // slope y/x
        val a = -8f
        // Current x being drawn
        val xMax = cam.project(vec3(model.time, 0f)).x
        // Where the marker will end up
        val target = cam.project(vec3(pos.x, pos.y))
        val b = a * xMax
        val y = a * target.x - b
        val div = if (buy) target.y else screenHeight - target.y
        val bouncedY = Interpolation.bounceOut.apply(y / div) * div
        batch.color = Color.WHITE
        val marker = if (buy) buyMarker else sellMarker
        batch.draw(marker, target.x - marker.packedWidth / 2,
                   if (buy) bouncedY - marker.packedHeight else screenHeight - bouncedY)
    }

    override fun dispose() {
        batch.dispose()  //TODO not if owned by game
        ui.dispose()
    }
}