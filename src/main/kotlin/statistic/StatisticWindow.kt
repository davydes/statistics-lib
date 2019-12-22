package statistic

import statistic.Statistic.Companion.EMPTY
import java.lang.Long.min
import java.time.Duration
import java.util.concurrent.TimeUnit

data class StatisticWindow(
    private val size: Size,
    private val basedOnNanos: Long,
    private val buckets: List<Statistic>
) : Window<Statistic> {

    override fun value(): Statistic = value(Duration.of(size.length.toLong(), size.unit.toChronoUnit()))

    override fun value(duration: Duration): Statistic {
        val actualWindow = tickIfNeeded(NanoTime.current())
        val ticks = actualWindow.size.unit.convert(duration).toInt()

        return actualWindow.buckets
            .take(ticks)
            .filter { !it.isEmpty }
            .fold(EMPTY) { total, current -> total + current }
    }

    override fun add(value: Statistic): StatisticWindow {
        val actualWindow = tickIfNeeded(NanoTime.current())
        val actualBuckets = actualWindow.buckets.toMutableList()
        actualBuckets[0] = (actualBuckets.firstOrNull() ?: EMPTY) + value
        return actualWindow.copy(buckets = actualBuckets)
    }

    private fun tickIfNeeded(nanoTime: Long): StatisticWindow {
        val elapsed = nanoTime - basedOnNanos
        if (elapsed < 0) throw IllegalStateException("now mustn't be less than window creation time")

        val ticksElapsed = size.unit.convert(elapsed, TimeUnit.NANOSECONDS)
        val remainder = elapsed - TimeUnit.NANOSECONDS.convert(ticksElapsed, size.unit)

        if (ticksElapsed == 0L) return this

        val newBucketsSize = min(ticksElapsed, size.length.toLong()).toInt()
        val newBuckets = (Array(newBucketsSize) { EMPTY }.toList() + buckets).take(size.length)

        return this.copy(
            basedOnNanos = nanoTime - remainder,
            buckets = newBuckets
        )
    }
}