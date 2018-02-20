package tibi.buysell

import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.math.ceil
import kotlin.math.max


class Model {

    var value = 200f
    val points = CircularFifoQueue<Vector2>(2000).apply { add(Vector2(time, value)) }
    var time = 0f

    var qty = 0
    var moneyLeft = 1_000f
    var boughtValue = 0f


    fun update(delta: Float) {
        time += delta
        // Pure random walk:
        value += random(-10f, +10f)
        // Now let's cheat a bit to avoid buying too low:
        if (value < 100) {
            value += (100 - value) / 100
        }
        if (value < 5f) {
            value = 5f
        }
        points.add(Vector2(time, value))
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

    fun clear() {
        value = 200f
        time = 0f
        points.clear()
        points.add(Vector2(time, value))
        qty = 0
        moneyLeft = 1_000f
        boughtValue = 0f
    }

    fun canBuy() = moneyLeft >= value
    fun canSell() = qty > 0

}