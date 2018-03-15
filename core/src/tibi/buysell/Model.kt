package tibi.buysell

import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.math.ceil
import kotlin.math.max


const val START_AMOUNT = 1_000f

class Model {

    var value = 0f
    val points = CircularFifoQueue<Vector2>(2000).apply { add(Vector2(time, value)) }
    var time = 0f

    var qty = 0
    var moneyLeft = 0f
    var boughtValue = 0f

    val volatility = 3f

    fun clear() {
        value = 200f
        time = 0f
        points.clear()
        points.add(Vector2(time, value))
        qty = 1
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
        val amountToBuy = moneyLeft / 3
        val qtyToBuy = max(1f, amountToBuy / value).toInt()
        boughtValue = (boughtValue * qty + value * qtyToBuy) / (qty + qtyToBuy)
        qty += qtyToBuy
        moneyLeft -= qtyToBuy * value
    }

    fun sell() {
        if (!canSell()) return
        val qtyToSell = ceil(qty / 5f).toInt()
        qty -= qtyToSell
        moneyLeft += qtyToSell * value
    }

    fun canBuy() = moneyLeft >= value
    fun canSell() = qty > 0

}