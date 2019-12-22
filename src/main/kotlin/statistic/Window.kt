package statistic

import java.time.Duration

/*
  Abstract window of any objects
*/
interface Window<T> {
    /*
      Whole window statistic value
    */
    fun value(): T

    /*
      Value for particular duration, but less than window size.
      Precision limited by window size unit.
    */
    fun value(duration: Duration): T

    /*
      Add new value to statistic window
    */
    fun add(value: T): Window<T>
}
