package codingchallenge

import java.io.File
import java.io.PrintWriter

class Sink(saveTo: String, contents: List[String] = List[String]()) {
  def put(entry: String): Sink = new Sink(saveTo, entry :: contents)

  def persist(destination: String = saveTo) = {
    val file = new PrintWriter(new File(destination))

    // @note that contents are stored in 'reversed' order. So it calls #reverse before saving to file
    // in order to write most recent entries at the bottom
    file.write(contents.reverse.mkString("\n"))
    file.close
  }
}

object Sink {
  def apply(saveTo: String): Sink = new Sink(saveTo)
}