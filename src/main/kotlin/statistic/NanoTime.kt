package statistic

import java.util.concurrent.atomic.AtomicLong

/*
  For easier testing
*/
internal object NanoTime {
    private val current = AtomicLong(-1)
    internal fun current(): Long = if (current.get() > 0) current.get() else System.nanoTime()
    internal fun withNanoTime(nanoTime: Long, block: () -> Unit) {
        val prevValue = current.get()
        try {
            current.set(nanoTime)
            block()
        } finally {
            current.set(prevValue)
        }
    }
}