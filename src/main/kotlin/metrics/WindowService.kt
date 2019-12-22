package metrics

import java.time.Duration

interface WindowService {
  fun extractMetric(nanoTime: Long, window: Window, duration: Duration): Metric
  fun addValue(nanoTime: Long, window: Window, value: Long): Window
}
