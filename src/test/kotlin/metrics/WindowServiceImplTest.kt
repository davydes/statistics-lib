package metrics

import metrics.Metric.Companion.EMPTY
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Duration
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.NANOSECONDS

class WindowServiceImplTest {
  private val service = WindowServiceImpl()

  @Test
  fun `extractMetric at the same moment`() {
    val now = System.nanoTime()
    val window = Window(
      size = Window.Size(unit = MINUTES, length = 15),
      createdAt = now,
      buckets = listOf(Metric(1, 10, 10, 10), Metric(1, 20, 20, 20))
    )

    assertThat(service.extractMetric(now, window, Duration.ofNanos(0))).isEqualTo(EMPTY)
    assertThat(service.extractMetric(now, window, Duration.ofSeconds(59))).isEqualTo(EMPTY)
    assertThat(service.extractMetric(now, window, Duration.ofMinutes(1))).isEqualTo(Metric(1, 10, 10, 10))
    assertThat(service.extractMetric(now, window, Duration.ofMinutes(2))).isEqualTo(Metric(2, 15, 20, 10))
    assertThat(service.extractMetric(now, window, Duration.ofMinutes(16))).isEqualTo(Metric(2, 15, 20, 10))
  }

  @Test
  fun `extractMetric after minute shifts window`() {
    val past = System.nanoTime()
    val now = past + NANOSECONDS.convert(Duration.ofMinutes(1))

    val window = Window(
      size = Window.Size(unit = MINUTES, length = 15),
      createdAt = past,
      buckets = listOf(Metric(1, 10, 10, 10), Metric(1, 20, 20, 20))
    )

    assertThat(service.extractMetric(now, window, Duration.ofNanos(0))).isEqualTo(EMPTY)
    assertThat(service.extractMetric(now, window, Duration.ofMinutes(1))).isEqualTo(EMPTY)
    assertThat(service.extractMetric(now, window, Duration.ofMinutes(2))).isEqualTo(Metric(1, 10, 10, 10))
    assertThat(service.extractMetric(now, window, Duration.ofMinutes(3))).isEqualTo(Metric(2, 15, 20, 10))
  }

  @Test
  fun `extractMetric after 16 minutes returns only empty metrics`() {
    val past = System.nanoTime()
    val now = past + NANOSECONDS.convert(Duration.ofMinutes(16))

    val window = Window(
      size = Window.Size(unit = MINUTES, length = 15),
      createdAt = past,
      buckets = listOf(Metric(1, 10, 10, 10), Metric(1, 20, 20, 20))
    )

    assertThat(service.extractMetric(now, window, Duration.ofMinutes(30))).isEqualTo(EMPTY)
  }

  @Test
  fun `addValue to metric at the same moment`() {
    val now = System.nanoTime()

    val window = Window(
      size = Window.Size(unit = MINUTES, length = 15),
      createdAt = now,
      buckets = listOf(Metric(1, 10, 10, 10), Metric(1, 20, 20, 20))
    )

    assertThat(service.addValue(now, window, 30))
      .isEqualTo(window.copy(buckets = listOf(
        Metric(2, 20, 30, 10),
        Metric(1, 20, 20, 20)
      )))
  }

  @Test
  fun `addValue to metric after minute`() {
    val past = System.nanoTime()
    val now = past + NANOSECONDS.convert(Duration.ofMinutes(1))

    val window = Window(
      size = Window.Size(unit = MINUTES, length = 15),
      createdAt = past,
      buckets = listOf(Metric(1, 10, 10, 10), Metric(1, 20, 20, 20))
    )

    assertThat(service.addValue(now, window, 30))
      .isEqualTo(window.copy(
        createdAt = now,
        buckets = listOf(
          Metric(1, 30, 30, 30),
          Metric(1, 10, 10, 10),
          Metric(1, 20, 20, 20)
        )))
  }

  @Test
  fun `addValue to metric after minute and half`() {
    val past = System.nanoTime()
    val now = past + NANOSECONDS.convert(Duration.ofSeconds(90))

    val window = Window(
      size = Window.Size(unit = MINUTES, length = 15),
      createdAt = past,
      buckets = listOf(Metric(1, 10, 10, 10), Metric(1, 20, 20, 20))
    )

    assertThat(service.addValue(now, window, 30))
      .isEqualTo(window.copy(
        createdAt = now - NANOSECONDS.convert(Duration.ofSeconds(30)),
        buckets = listOf(
          Metric(1, 30, 30, 30),
          Metric(1, 10, 10, 10),
          Metric(1, 20, 20, 20)
        )))
  }
}