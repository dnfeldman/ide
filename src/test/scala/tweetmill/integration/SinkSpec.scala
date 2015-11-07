package tweetmill.integration

import org.scalatest.FunSpec
import tweetmill.Sink

class SinkSpec extends FunSpec {

  describe("Source") {
    val filename = "src/test/scala/tweetmill/integration/files/test_output.txt"
    val sink = new Sink(filename)

    it("prepends new record to its contents") {
      val newSink = sink.put("first").put("second")
      assert(newSink.contents == List("second", "first"))
    }

    it("saves contents to file in the correct order") {
      val newSink = sink.put("first").put("second")
      newSink.persist()

      val fileContents = scala.io.Source.fromFile(filename).getLines.mkString(", ")
      assert(fileContents == "first, second")

      // Clean up after itself
      val file = new java.io.File(filename)
      if (file.exists) file.delete
    }
  }
}
