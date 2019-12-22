package metrics

import java.util.concurrent.TimeUnit

data class Window(
  val size: Size,
  val createdAt: Long,
  val buckets: List<Metric>
) {
  data class Size(
    val unit: TimeUnit,
    val length: Int
  )
}
