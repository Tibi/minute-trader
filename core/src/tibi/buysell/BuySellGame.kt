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

        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        cam = OrthographicCamera(500f, 500f * height / width)
        cam.position.set(100f, 100f, 0f)
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

    override fun render() {

        Gdx.gl.glClearColor(.9f, .95f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (!paused) model.update()
        if (model.values.size < 2) return

        // Let the camera follow the curve
        cam.position.x = model.time - 200
        val y = model.value
        if (y + 20 > cam.position.y + cam.viewportHeight / 2) {
            cam.position.y++
        } else if (y - 20 < cam.position.y - cam.viewportHeight / 2) {
            cam.position.y--
        }
        cam.update()

        shape.projectionMatrix = cam.combined
        shape.begin(ShapeRenderer.ShapeType.Line)
        drawAxis()

        shape.setColor(.1f, .1f, .5f, 1f)
        var x = model.time - model.values.size
        model.values.windowed(2).forEach { vals ->
            shape.drawThick(x, vals[0], x+1, vals[1], true)
            x++
        }
        shape.end()

        batch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        batch.begin()
        val xText = 20f
        font.color = blue
//        font.draw(batch, format("Value", model.value), xText, 450f)
        font.draw(batch, format("Total", model.moneyTotal), xText, 400f)
        font.draw(batch, format("Qty", model.qty.toFloat()), xText, 350f)
        font.draw(batch, format("Left", model.moneyLeft), xText, 150f)
        batch.end()
    }

    fun format(txt: String, f: Float) = "$txt:\t%5d".format(f.toInt())

    val blue = Color(.31f, .31f, 1f, 1f)
    val blueLight = Color(.7f, .8f, 1f, 1f)
    val red = Color.valueOf("FF6666")

    private fun drawAxis() {

        batch.projectionMatrix = cam.combined
        batch.begin()

        // World coordinates of the screen viewport
        val start = cam.unproject(Vector3(0f, width, 0f))
        val end = cam.unproject(Vector3(width, 0f, 0f))

        // Horizontal lines
        var y = 0f
        while (y < end.y) {
            val thick = y.toInt() % 100 == 0
            shape.drawThick(start.x, y, end.x, y, y == 0f, if (thick) blue else blueLight)
            if (thick) {
                font.draw(batch, y.toInt().toString(), end.x - 30, y+15, 20f, Align.right, false)
            }
            y += 20
        }

        batch.end()

        // Vertical lines
        var x = 0f
        while (x < end.x) {
            val thick = x.toInt() % 100 == 0
            shape.drawThick(x, start.y, x, end.y, x == 0f, if (thick) blue else blueLight)
            x += 20
        }

        // Bought value line
        if (model.qty > 0) {
            shape.drawThick(start.x, model.boughtValue, end.x, model.boughtValue, true, red)
        }
    }

    fun ShapeRenderer.drawThick(x1: Float, y1: Float, x2: Float, y2: Float, thick: Boolean = false, color: Color = blue) {
        if (thick) {
            rectLine(x1, y1, x2, y2, 1f, color, color)
        } else {
            this.color = color
            line(x1, y1, x2, y2)
        }
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shape.dispose()
    }
}
