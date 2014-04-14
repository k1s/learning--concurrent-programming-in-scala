package org.learningconcurrency
package ch2






object VolatileScan extends App {
  val document: Seq[String] = for (i <- 1 to 5) yield "lorem ipsum " * (100 - 20 * i) + "Leslie"
  val query = "Leslie"
  var results = Array.fill(document.length)(-1)
  @volatile var found = false
  val threads = for (i <- 0 until document.length) yield thread {
    def scan(n: Int, words: Seq[String]): Unit =
      if (words(n) == query) {
        results(i) = n
        found = true
      } else if (!found) scan(n + 1, words)
    scan(0, document(i).split(" "))
  }
  for (t <- threads) t.join()
  println(s"Found $query: ${results.find(_ != -1)}")
}


object VolatileSharedStateAccess extends App {
  for (i <- 0 until 10000) {
    @volatile var t1started = false
    @volatile var t2started = false
    var t1index = -1
    var t2index = -1
  
    val t1 = thread {
      Thread.sleep(1)
      t1started = true
      t2index = if (t2started) 0 else 1
    }
    val t2 = thread {
      Thread.sleep(1)
      t2started = true
      t1index = if (t1started) 0 else 1
    }
  
    t1.join()
    t2.join()
    assert(!(t1index == 1 && t2index == 1), s"t1 = $t1index, t2 = $t2index")
  }
}
