package metrics

import java.lang.Long.max
import java.lang.Long.min

data class Metric(
  val count: Long = 0L,
  val avg: Long = 0L,
  val max: Long = 0L,
  val min: Long = 0L
) {
  val isEmpty get() = count == 0L

  operator fun plus(metric: Metric): Metric {
    if (isEmpty) return metric
    if (metric.isEmpty) return this

    return Metric(
      count = count + metric.count,
      avg = (avg * count + metric.avg * metric.count) / (count + metric.count),
      min = min(min, metric.min),
      max = max(max, metric.max)
    )
  }

  companion object {
    val EMPTY = Metric()
    fun ofValue(value: Long) = Metric(1, value, value, value)
  }
}