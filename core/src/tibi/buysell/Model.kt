package tibi.buysell

import com.badlogic.gdx.math.MathUtils.random
import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.math.max

class Model {

    val volatility = 10f

    var qty = 0
    val moneyInvested get() = qty * value
    var moneyLeft = 1000f
    val moneyTotal get() = moneyLeft + moneyInvested

    private val SIZE = 1000

    var time = 0f
    val values = CircularFifoQueue<Float>(SIZE).apply { add(10f) }
    var value = 10f

    var boughtValue = 0f

    fun update() {
        time++
//        val delta = value * random(-volatility, +volatility*1.01f) / 100
        val delta = random(-volatility, +volatility)
        value = max(1f, value + delta)
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
        val qtyToSell = max(1, qty / 10)
        qty -= qtyToSell
        moneyLeft += qtyToSell * value
    }

}