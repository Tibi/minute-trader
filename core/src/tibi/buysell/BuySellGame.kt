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
import kotlin.math.max
import kotlin.math.min

class BuySellGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var font: BitmapFont

    private val MAX = 100000
    val points = Array(MAX, { 0f })
    var i = 2
    var x = 0
    var y = 250
    val volatility = 5f  // in %

    var moneyInvested = 0
    var moneyLeft = 500
    val moneyTotal get() = moneyLeft + moneyInvested
    var investAmount = 100

    private lateinit var cam: OrthographicCamera

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        font = BitmapFont()

        val w = Gdx.graphics.getWidth()
        val h = Gdx.graphics.getHeight()
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

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        x += 1
        val delta = random(-volatility, +volatility)
        moneyInvested += (moneyInvested * delta / 100).toInt()
        y += (y * delta / 100).toInt()

        points[i++] = x.toFloat()
        points[i++] = y.toFloat()

        cam.position.x = x.toFloat() - 200
        if (y + 20 > cam.position.y + cam.viewportHeight / 2) {
            cam.position.y++
        } else if (y - 20 < cam.position.y - cam.viewportHeight / 2) {
            cam.position.y--
        }
        cam.update()

        shapeRenderer.projectionMatrix = cam.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        shapeRenderer.setColor(.31f, .31f, 1f, 1f)
        shapeRenderer.line(0f, 0f, 10000f, 0f)
        shapeRenderer.line(0f, 0f, 0f, 10000f)

        shapeRenderer.setColor(1f, 1f, 1f, 1f)
        shapeRenderer.polyline(points.toFloatArray(), max(0, i - 500), min(i, 500))
        shapeRenderer.end()

        batch.projectionMatrix = cam.combined
        batch.begin()
        val xText = x - 420f
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
        shapeRenderer.dispose()
    }
}
