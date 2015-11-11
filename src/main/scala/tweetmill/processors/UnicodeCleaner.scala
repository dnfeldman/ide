package tweetmill.processors

import org.joda.time.format.DateTimeFormat
import tweetmill.utils.TextCleaner
import tweetmill.{Sink, Tweet}

class UnicodeCleaner(val sink: Sink, val tweetsWithUnicode: Long = 0L) extends Processable with TextCleaner {

  def process(tweet: Tweet): UnicodeCleaner = {
    val originalText = tweet.text
    val nonUnicodeText = removeUnicodeCharacters(originalText)

    val newTweetsWithUnicode = if (isSameLength(originalText, nonUnicodeText)) 0L else 1L

    val cleanText = replaceWhitespaceCharacters(nonUnicodeText)

    val formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withZoneUTC()
    val entry = cleanText + " (timestamp: " + formatter.print(tweet.createdAt) + ")"

    new UnicodeCleaner(sink.put(entry), tweetsWithUnicode + newTweetsWithUnicode)
  }

  def terminate = sink.put(footer).persist()

  private[this] def isSameLength(left: String, right: String): Boolean = left.length == right.length
  private[this] def footer: String = "\n\n" + tweetsWithUnicode + " tweets contained unicode."
}

object UnicodeCleaner {
  def apply(sink: Sink): UnicodeCleaner = new UnicodeCleaner(sink)
}