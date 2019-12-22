package statistic

import java.time.Duration

interface Window<T> {
    fun value(): T
    fun value(duration: Duration): T
    fun add(value: T): Window<T>
}
