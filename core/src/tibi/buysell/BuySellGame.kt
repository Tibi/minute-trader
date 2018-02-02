package tibi.buysell

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

import com.badlogic.gdx.math.MathUtils.random

class BuySellGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer

    val points = Array(1000, { 0f })
    var i = 2
    var x = 0
    var y = 250
    val volatility = 5

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)


        x += 1
        y += random(-volatility, +volatility)
        points[i++] = x.toFloat()
        points[i++] = y.toFloat()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.setColor(1f, 1f, 1f, 1f)
        shapeRenderer.polyline(points.toFloatArray(), 0, i)
        shapeRenderer.end()

        if (i == 1000) {
            i = 2
            x = 0
            y = 250
        }


    }

    override fun dispose() {
        batch.dispose()
    }
}
