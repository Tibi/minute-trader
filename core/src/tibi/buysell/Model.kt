package tibi.buysell

import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import org.apache.commons.collections4.queue.CircularFifoQueue


const val START_AMOUNT = 1_000f

class Model {

    var value = 0f
    var time = 0f
    val points = CircularFifoQueue<Vector2>(2000).apply { add(Vector2(time, value)) }
    val buys = mutableListOf<Vector2>()
    val sells = mutableListOf<Vector2>()

    var qty = 0
    var moneyLeft = 0f
    var boughtValue = 0f

    val volatility = 3f

    fun clear() {
        value = 100f
        time = 0f
        points.clear()
        points.add(Vector2(time, value))
        buys.clear()
        sells.clear()
        qty = 5
        moneyLeft = START_AMOUNT - qty * value
        boughtValue = value
    }

    fun update(delta: Float) {
        time += delta
        // r is the % to go up or down, it's proportional to time elapsed and volatility
        val r = volatility * random(-delta, +delta)
        // Going up, simply add r %
        if (r > 0) value += value * r
        // Goes down to the value that requires r % to go up to the current value
        else value /= 1 - r
        points.add(vec2(time, value))
    }

    fun buy() {
        if (!canBuy()) return
        boughtValue = (boughtValue * qty + value) / (qty + 1)
        qty++
        moneyLeft -= value
        buys.add(vec2(time, value))
    }

    fun sell() {
        if (!canSell()) return
        qty--
        moneyLeft += value
        sells.add(vec2(time, value))
    }

    fun sellAll() {
        if (!canSell()) return
        moneyLeft += value * qty
        qty = 0
    }

    fun canBuy() = moneyLeft >= value
    fun canSell() = qty > 0

}