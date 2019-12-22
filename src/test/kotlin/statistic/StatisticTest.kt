package statistic

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import statistic.Statistic.Companion.EMPTY

class StatisticTest {
    @Test
    fun `plus should return new aggregated statistic and does it commutatively`() {
        val statistic1 = Statistic(10, 100, 200, 10)
        val statistic2 = Statistic(30, 150, 400, 15)

        assertThat(statistic1 + statistic2).isEqualTo(Statistic(40, 137, 400, 10))
        assertThat(statistic2 + statistic1).isEqualTo(Statistic(40, 137, 400, 10))
    }

    @Test
    fun `empty plus not empty should return not empty statistic and does it commutatively`() {
        val statistic = Statistic(10, 100, 200, 10)

        assertThat(statistic + EMPTY).isEqualTo(statistic)
        assertThat(EMPTY + statistic).isEqualTo(statistic)
    }

    @Test
    fun `empty plus empty is empty`() {
        assertThat(EMPTY + EMPTY).isEqualTo(EMPTY)
    }
}