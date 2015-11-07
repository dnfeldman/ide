package tweetmill

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import tweetmill.processors.{UnicodeCleaner, HashtagGraphAnalyzer}
import tweetmill.utils.{Graph, TimeWindow}

trait HelperMethods {

  def makeTweet(
      arrivalSecond: String = "00",
      text: String = "Some text",
      hashtags: List[String] = List[String]()): Tweet = {

    new Tweet(seconds(arrivalSecond), text, makeHashtags(hashtags))
  }

  def makeHashtagGraphAnalyzer: HashtagGraphAnalyzer = {
    val defaultSink = new Sink("/dev/null")
    val timeWindow = new TimeWindow(lengthInSeconds = 5)

    new HashtagGraphAnalyzer(defaultSink, timeWindow, Graph())
  }

  def makeUnicodeCleaner: UnicodeCleaner = {
    val defaultSink = new Sink("/dev/null")

    new UnicodeCleaner(defaultSink)
  }

  private def seconds(s: String): DateTime = {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z")

    val datePrefix = "2015-06-05 20:00:"
    formatter.parseDateTime(datePrefix + s + " +0000")
  }

  private def makeHashtags(tags: List[String]): List[Hashtag] = {
    val hashtagList = List[Hashtag]()
    tags.foldLeft(hashtagList)((hl, tag) => Hashtag(tag) +: hl)
  }
}