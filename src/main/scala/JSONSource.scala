import java.lang.RuntimeException
import java.text.SimpleDateFormat

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

class JSONSource(filename: String) {
  val stream = Source.fromFile(filename).getLines()
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("EEE MMM dd H:m:s Z yyyy")
  }

  lazy val tweets: List[Tweet] = stream.toList.collect { line => extractTweet(line) match {
    case Some(tweet) => tweet
    }
  }

  def extractTweet(line: String): Option[Tweet] = {
    val json = parse(line)
    if (((json \ "created_at") != JNothing) && ((json \ "text") != JNothing)) {
      Some(json.extract[Tweet])
    } else {
      None
    }
  }
}

object JSONSource {
  val source = new JSONSource("data/tweets.txt")
  val sink = new Sink("data/output.txt")

  val tweets = TweetCollection(source.tweets)

  def run: Unit = sink.put(tweets)

}


case class Tweet(created_at: java.util.Date, text: String) {
  def removeUnicode: String = text.replaceAll("[^\\p{ASCII}]|\n", "")

  def mkString: String = cleanText + " (timestamp: " + created_at + ")"

  val cleanText: String = removeUnicode
  val hasUnicode: Boolean = if (cleanText.length() == text.length()) true else false
  val emptyString = ""
}


case class TweetCollection(tweets: List[Tweet]) {
  val tweetsWithUnicodeCount = tweets.foldLeft(0) {
    (tweetsWithUnicode, tweet) => tweetsWithUnicode + (if (tweet.hasUnicode) 1 else 0)
  }

  def mkString: String = tweets.map(tweet => tweet.mkString).mkString("\n") + "\n\n" + footer

  def footer: String = tweetsWithUnicodeCount + " tweets contained unicode.\n"
}