package statistic

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import statistic.NanoTime.current
import statistic.NanoTime.withNanoTime
import statistic.Statistic.Companion.EMPTY
import statistic.Statistic.Companion.ofValue
import java.time.Duration
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.NANOSECONDS

class StatisticWindowTest {
    @Test
    fun `value at the same moment`() {
        val now = System.nanoTime()
        val window = StatisticWindow(
            size = Size(unit = MINUTES, length = 15),
            basedOnNanos = now,
            buckets = listOf(Statistic(1, 10, 10, 10), Statistic(1, 20, 20, 20))
        )

        withNanoTime(now) {
            assertThat(window.value(Duration.ofNanos(0))).isEqualTo(EMPTY)
            assertThat(window.value(Duration.ofSeconds(59))).isEqualTo(EMPTY)
            assertThat(window.value(Duration.ofMinutes(1))).isEqualTo(Statistic(1, 10, 10, 10))
            assertThat(window.value(Duration.ofMinutes(2))).isEqualTo(Statistic(2, 15, 20, 10))
            assertThat(window.value(Duration.ofMinutes(16))).isEqualTo(Statistic(2, 15, 20, 10))
        }
    }

    @Test
    fun `value after a minute`() {
        val past = System.nanoTime()

        val window = StatisticWindow(
            size = Size(unit = MINUTES, length = 15),
            basedOnNanos = past,
            buckets = listOf(Statistic(1, 10, 10, 10), Statistic(1, 20, 20, 20))
        )

        val now = past + NANOSECONDS.convert(Duration.ofMinutes(1))

        withNanoTime(now) {
            assertThat(window.value(Duration.ofNanos(0))).isEqualTo(EMPTY)
            assertThat(window.value(Duration.ofMinutes(1))).isEqualTo(EMPTY)
            assertThat(window.value(Duration.ofMinutes(2))).isEqualTo(Statistic(1, 10, 10, 10))
            assertThat(window.value(Duration.ofMinutes(3))).isEqualTo(Statistic(2, 15, 20, 10))
        }
    }

    @Test
    fun `value after 16 minutes returns only empty values`() {
        val past = System.nanoTime()

        val window = StatisticWindow(
            size = Size(unit = MINUTES, length = 15),
            basedOnNanos = past,
            buckets = listOf(Statistic(1, 10, 10, 10), Statistic(1, 20, 20, 20))
        )

        withNanoTime(past + NANOSECONDS.convert(Duration.ofMinutes(16))) {
            assertThat(window.value(Duration.ofMinutes(30))).isEqualTo(EMPTY)
        }
    }

    @Test
    fun `add new value to window at the same moment`() {
        val now = System.nanoTime()

        val window = StatisticWindow(
            size = Size(unit = MINUTES, length = 15),
            basedOnNanos = now,
            buckets = listOf(Statistic(1, 10, 10, 10), Statistic(1, 20, 20, 20))
        )

        withNanoTime(now) {
            assertThat(window.add(ofValue(30)))
                .isEqualTo(
                    window.copy(
                        buckets = listOf(
                            Statistic(2, 20, 30, 10),
                            Statistic(1, 20, 20, 20)
                        )
                    )
                )
        }
    }

    @Test
    fun `add value to window after a minute`() {
        val past = System.nanoTime()

        val window = StatisticWindow(
            size = Size(unit = MINUTES, length = 15),
            basedOnNanos = past,
            buckets = listOf(Statistic(1, 10, 10, 10), Statistic(1, 20, 20, 20))
        )

        withNanoTime(past + NANOSECONDS.convert(Duration.ofMinutes(1))) {
            assertThat(window.add(ofValue(30))).isEqualTo(
                window.copy(
                    basedOnNanos = current(),
                    buckets = listOf(
                        Statistic(1, 30, 30, 30),
                        Statistic(1, 10, 10, 10),
                        Statistic(1, 20, 20, 20)
                    )
                )
            )
        }
    }

    @Test
    fun `add value to window after a minute and half`() {
        val past = System.nanoTime()
        val now = past + NANOSECONDS.convert(Duration.ofSeconds(90))

        val window = StatisticWindow(
            size = Size(unit = MINUTES, length = 15),
            basedOnNanos = past,
            buckets = listOf(Statistic(1, 10, 10, 10), Statistic(1, 20, 20, 20))
        )

        withNanoTime(now) {
            assertThat(window.add(ofValue(30)))
                .isEqualTo(
                    window.copy(
                        basedOnNanos = now - NANOSECONDS.convert(Duration.ofSeconds(30)),
                        buckets = listOf(
                            Statistic(1, 30, 30, 30),
                            Statistic(1, 10, 10, 10),
                            Statistic(1, 20, 20, 20)
                        )
                    )
                )
        }
    }
}