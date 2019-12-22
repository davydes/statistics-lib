package metrics

import metrics.Metric.Companion.EMPTY
import metrics.Metric.Companion.ofValue
import java.lang.Long.min
import java.time.Duration
import java.util.concurrent.TimeUnit

class WindowServiceImpl : WindowService {
  override fun extractMetric(nanoTime: Long, window: Window, duration: Duration): Metric {
    val actualWindow = window.tickIfNeeded(nanoTime)
    val ticks = actualWindow.size.unit.convert(duration).toInt()

    return actualWindow.buckets
      .take(ticks)
      .filter { !it.isEmpty }
      .fold(EMPTY) { total, current -> total + current }
  }

  override fun addValue(nanoTime: Long, window: Window, value: Long): Window {
    val actualWindow = window.tickIfNeeded(nanoTime)
    val actualBuckets = actualWindow.buckets.toMutableList()
    actualBuckets[0] = (actualBuckets.firstOrNull() ?: EMPTY) + ofValue(value)
    return actualWindow.copy(buckets = actualBuckets)
  }

  private fun Window.tickIfNeeded(nanoTime: Long): Window {
    val elapsed = nanoTime - createdAt
    if (elapsed < 0) throw IllegalStateException("now mustn't be less than window creation time")

    val ticksElapsed = size.unit.convert(elapsed, TimeUnit.NANOSECONDS)
    val remainder = elapsed - TimeUnit.NANOSECONDS.convert(ticksElapsed, size.unit)

    if (ticksElapsed == 0L) return this

    val newBucketsSize = min(ticksElapsed, size.length.toLong()).toInt()
    val newBuckets = (Array(newBucketsSize) { EMPTY }.toList() + buckets).take(size.length)

    return this.copy(
      createdAt = nanoTime - remainder,
      buckets = newBuckets
    )
  }
}