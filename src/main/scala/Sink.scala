import java.io.File
import java.io.PrintWriter
import scala.io.Source

class Sink(destination: String) {
  def put(data: TweetCollection) = {
    val file = new PrintWriter(new File(destination))
    file.write(data.mkString)
    file.close
  }
}
