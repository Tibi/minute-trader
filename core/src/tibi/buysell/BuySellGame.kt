package tibi.buysell

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector3
import kotlin.math.max
import kotlin.math.min

class BuySellGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var shape: ShapeRenderer
    lateinit var font: BitmapFont

    private val MAX = 100000
    val points = Array(MAX, { 0f })
    var i = 2
    var x = 0f
    var y = 250f
    val volatility = 5f  // in %

    var moneyInvested = 0f
    var moneyLeft = 500f
    val moneyTotal get() = moneyLeft + moneyInvested
    var investAmount = 100f

    private lateinit var cam: OrthographicCamera

    override fun create() {
        batch = SpriteBatch()
        shape = ShapeRenderer()
        font = BitmapFont()

        val w = Gdx.graphics.width
        val h = Gdx.graphics.height
		cam = OrthographicCamera(500f, 500f * h / w)
		cam.position.set(250f, 250f, 0f)
        cam.update()

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun keyTyped(character: Char) =
                when (character) {
                    'b' -> { buy(); true }
                    's' -> { sell(); true }
                    else -> false
                }
        }
    }

    var deltaCumul = 0f

    override fun render() {

//        deltaCumul += Gdx.graphics.deltaTime
//        Gdx.app.log("", deltaCumul.toString())
//        if (deltaCumul < .2f) return
//        deltaCumul = 0f

        Gdx.gl.glClearColor(.8f, .9f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        x += 1
        val delta = random(-volatility, +volatility) //+ .2f
        moneyInvested += moneyInvested * delta / 100
        y += y * delta / 100

        points[i++] = x
        points[i++] = y

        cam.position.x = x - 200
        if (y + 20 > cam.position.y + cam.viewportHeight / 2) {
            cam.position.y++
        } else if (y - 20 < cam.position.y - cam.viewportHeight / 2) {
            cam.position.y--
        }
        cam.update()

//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.projectionMatrix = cam.combined
        shape.begin(ShapeRenderer.ShapeType.Line)

        drawAxis()

        shape.setColor(.1f, .1f, .5f, 1f)
        shape.polyline(points.toFloatArray(), max(0, i - 600), min(i, 600))
        shape.end()

//        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin()
        val xText = 20f
        font.draw(batch, "Value \t $y", xText, 450f)
        font.draw(batch, "Total \t $moneyTotal", xText, 400f)
        font.draw(batch, "Amount \t $investAmount", xText, 350f)
        font.draw(batch, "Invested \t $moneyInvested", xText, 200f)
        font.draw(batch, "Left \t ${moneyTotal - moneyInvested}", xText, 150f)
        batch.end()

        if (i == MAX) {
            Gdx.app.exit()
        }
    }

    private fun drawAxis() {
        shape.setColor(.31f, .31f, 1f, 1f)
        shape.line(0f, 0f, 10000f, 0f)
        shape.line(0f, 0f, 0f, 10000f)

        shape.setColor(.7f, .8f, 1f, 1f)
        val start = cam.unproject(Vector3(0f, Gdx.graphics.height.toFloat(), 0f))
        val end = cam.unproject(Vector3(Gdx.graphics.width.toFloat(), 0f, 0f))
        var y = start.y
        while (y < end.y) {
            shape.line(start.x, y, end.x, y)
            y += 20
        }
    }

    fun buy() {
        if (moneyLeft > investAmount) {
            moneyInvested += investAmount
            moneyLeft -= investAmount
        }
//        updateInvestAmount()
    }

    fun sell() {
        if (moneyInvested > investAmount) {
            moneyInvested -= investAmount
            moneyLeft += investAmount
//        updateInvestAmount()
        }
    }

    private fun updateInvestAmount() {
        if (investAmount / 5 > moneyTotal) {
            investAmount /= 10
        } else if (investAmount * 5 < moneyTotal) {
            investAmount *= 10
        }
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shape.dispose()
    }
}
