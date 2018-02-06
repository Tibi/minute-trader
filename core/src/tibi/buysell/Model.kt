package tibi.buysell

import com.badlogic.gdx.math.MathUtils.random
import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.math.ceil
import kotlin.math.max


class Model {

    var value = 200f
    val values = CircularFifoQueue<Float>(2000).apply { add(value) }
    var time = 0f

    var qty = 0
    var moneyLeft = 1_000f
    var boughtValue = 0f


    fun update() {
        time++
        // Pure random walk:
        value += random(-10f, +10f)
        // Now let's cheat a bit to avoid buying too low:
        if (value < 100) {
            value += (100 - value) / 100
        }
        if (value < 5f) {
            value = 5f
        }
        values.add(value)
    }

    fun buy() {
        if (moneyLeft < value) return
        val amountToBuy = moneyLeft / 3
        val qtyToBuy = max(1f, amountToBuy / value).toInt()

        boughtValue = (boughtValue * qty + value * qtyToBuy) / (qty + qtyToBuy)

        qty += qtyToBuy
        moneyLeft -= qtyToBuy * value

    }

    fun sell() {
        if (qty == 0) return
        val qtyToSell = ceil(qty / 10f).toInt()
        qty -= qtyToSell
        moneyLeft += qtyToSell * value
    }

}