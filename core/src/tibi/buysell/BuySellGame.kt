package tibi.buysell

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align

class BuySellGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var shape: ShapeRenderer
    lateinit var font: BitmapFont
    private lateinit var cam: OrthographicCamera

    private var width = 0f
    private var height = 0f

    var paused = false
    val model = Model()

    override fun create() {
        batch = SpriteBatch()
        shape = ShapeRenderer()
        font = BitmapFont()

        resize(Gdx.graphics.width, Gdx.graphics.height)
        cam = OrthographicCamera(500f, 500f * height / width)
        cam.position.set(220f, 160f, 0f)
        cam.update()

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun keyDown(key: Int): Boolean {
                when (key) {
                    Keys.B -> model.buy()
                    Keys.S -> model.sell()
                    Keys.P -> paused = paused.not()
                    else -> return false
                }
                return true
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width.toFloat()
        this.height = height.toFloat()
    }

    override fun render() {

        Gdx.gl.glClearColor(.9f, .95f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (!paused) model.update()
        if (model.values.size < 2) return

        // Let the camera follow the curve
        if (model.time > cam.position.x + cam.viewportWidth / 2 - 50) {
            cam.position.x++
        }
        val y = model.value
        if (y + 50 > cam.position.y + cam.viewportHeight / 2) {
            cam.position.y++
        } else if (y - 50 < cam.position.y - cam.viewportHeight / 2) {
            cam.position.y--
        }
        cam.update()

        shape.projectionMatrix = cam.combined
        shape.begin(ShapeRenderer.ShapeType.Line)
        drawAxis()

        shape.setColor(.1f, .1f, .5f, 1f)
        var x = model.time - model.values.size
        model.values.windowed(2).forEach { vals ->
            draw(x, vals[0], x+1, vals[1], true, blue)
            x++
        }
        shape.end()

        // Attempt to draw a text background, TODO!
        shape.projectionMatrix.setToOrtho2D(0f, 0f, width, height)
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = blueLight
        shape.rect(5f, height - 80, 60f, height - 5)
        shape.end()

        batch.begin()
        val xText = 20f
        font.color = blue
        font.draw(batch, "Qty:", xText, height - 30, 30f, Align.right, false)
        font.draw(batch, "Total:", xText, height - 50, 30f, Align.right, false)
        font.draw(batch, "Left:", xText, height - 70, 30f, Align.right, false)
        val xValues = 70f
        font.draw(batch, "${model.qty}", xValues, height - 30, 30f, Align.right, false)
        font.draw(batch, "%5d $".format(model.moneyTotal.toInt()), xValues, height - 50, 30f, Align.right, false)
        font.draw(batch, "%5d $".format(model.moneyLeft.toInt()), xValues, height - 70, 30f, Align.right, false)
        batch.end()
    }

    val blue = Color(.31f, .31f, 1f, 1f)
    val blueLight = Color(.7f, .8f, 1f, 1f)
    val red = Color.valueOf("FF6666")

    private fun drawAxis() {

        font.color = blueLight

        // World coordinates of the screen viewport
        val start = cam.unproject(Vector3(0f, width, 0f))
        val end = cam.unproject(Vector3(width, 0f, 0f))
        val coarseGrid = 100
        val fineGrid = 25

        // Vertical lines
        var x = start.x - start.x % fineGrid
        while (x < end.x) {
            val onCoarseGrid = x.toInt() % coarseGrid == 0
            draw(x, start.y, x, end.y, x == 0f, if (onCoarseGrid) blue else blueLight)
            x += fineGrid
        }

        // Horizontal lines
        batch.begin()
        var y = start.y - start.y % fineGrid
        while (y < end.y) {
            val onCoarseGrid = y.toInt() % coarseGrid == 0
            draw(start.x, y, end.x, y, y == 0f, if (onCoarseGrid) blue else blueLight)
            if (onCoarseGrid) {
                // Label
                val p = cam.project(Vector3(end.x, y, 0f))
                font.draw(batch, y.toInt().toString(), p.x - 10, p.y + 16, 0f, Align.right, false)
            }
            y += fineGrid
        }
        batch.end()

        // Bought value line
        if (model.qty > 0) {
            draw(start.x, model.boughtValue, end.x, model.boughtValue, true, red)
        }
    }

    fun draw(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean, color: Color) {
        shape.color = color
        shape.line(x1, y1, x2, y2)
        if (thick) {
            shape.line(x1+1, y1+1, x2+1, y2+1)
        } else {
        }
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shape.dispose()
    }
}
