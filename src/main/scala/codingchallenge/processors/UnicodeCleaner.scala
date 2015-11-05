package codingchallenge.processors

import codingchallenge.{Sink, Tweet}

class UnicodeCleaner(sink: Sink, tweetsWithUnicode: Int = 0) extends TweetProcessor {

  def process(tweet: Tweet): UnicodeCleaner = {
    val originalText = tweet.text
    val cleanText = removeUnicode(originalText)

    val newTweetsWithUnicode = if (isSameLength(originalText, cleanText)) 1 else 0

    val entry = cleanText + " (timestamp: " + tweet.createdAt + ")"

    new UnicodeCleaner(sink.put(entry), tweetsWithUnicode + newTweetsWithUnicode)
  }

  def terminate = sink.put(footer).persist()

  private def removeUnicode(text: String): String = text.replaceAll("[^\\p{ASCII}]", "")
  private def isSameLength(left: String, right: String): Boolean = left.length == right.length
  private def footer: String = "\n\n" + tweetsWithUnicode + " tweets contained unicode."
}

object UnicodeCleaner {
  def apply(sink: Sink): UnicodeCleaner = new UnicodeCleaner(sink)
}