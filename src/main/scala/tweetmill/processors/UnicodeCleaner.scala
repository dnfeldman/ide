package tweetmill.processors

import org.joda.time.format.DateTimeFormat
import tweetmill.{Sink, Tweet}

class UnicodeCleaner(val sink: Sink, val tweetsWithUnicode: Long = 0L) extends Processable {

  def process(tweet: Tweet): UnicodeCleaner = {
    val originalText = tweet.text
    val cleanText = removeUnicode(originalText)

    val newTweetsWithUnicode = if (isSameLength(originalText, cleanText)) 0L else 1L

    val formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withZoneUTC()
    val entry = cleanText + " (timestamp: " + formatter.print(tweet.createdAt) + ")"

    new UnicodeCleaner(sink.put(entry), tweetsWithUnicode + newTweetsWithUnicode)
  }

  def terminate = sink.put(footer).persist()

  private[this] def removeUnicode(text: String): String = text.replaceAll("[^\\p{ASCII}]", "")
  private[this] def isSameLength(left: String, right: String): Boolean = left.length == right.length
  private[this] def footer: String = "\n\n" + tweetsWithUnicode + " tweets contained unicode."
}

object UnicodeCleaner {
  def apply(sink: Sink): UnicodeCleaner = new UnicodeCleaner(sink)
}