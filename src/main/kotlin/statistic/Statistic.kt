package statistic

import java.lang.Long.max
import java.lang.Long.min

data class Statistic(
    val count: Long,
    val avg: Long,
    val max: Long,
    val min: Long
) {
    val isEmpty get() = count == 0L

    operator fun plus(statistic: Statistic): Statistic {
        if (isEmpty) return statistic
        if (statistic.isEmpty) return this

        return Statistic(
            count = count + statistic.count,
            avg = (avg * count + statistic.avg * statistic.count) / (count + statistic.count),
            min = min(min, statistic.min),
            max = max(max, statistic.max)
        )
    }

    companion object {
        val EMPTY = Statistic(0, 0, 0, 0)
        fun ofValue(value: Long) = Statistic(1, value, value, value)
    }
}