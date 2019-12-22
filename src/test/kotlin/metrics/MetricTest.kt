package metrics

import metrics.Metric.Companion.EMPTY
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MetricTest {
  @Test
  fun `plus should return new aggregated metric and does it commutatively`() {
    val metric1 = Metric(10, 100, 200, 10)
    val metric2 = Metric(30, 150, 400, 15)

    assertThat(metric1 + metric2).isEqualTo(Metric(40, 137, 400, 10))
    assertThat(metric2 + metric1).isEqualTo(Metric(40, 137, 400, 10))
  }

  @Test
  fun `empty plus not empty should return not empty metric and does it commutatively`() {
    val metric = Metric(10, 100, 200, 10)

    assertThat(metric + EMPTY).isEqualTo(metric)
    assertThat(EMPTY + metric).isEqualTo(metric)
  }

  @Test
  fun `empty plus empty is empty`() {
    assertThat(EMPTY + EMPTY).isEqualTo(EMPTY)
  }
}