package tweetmill

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import tweetmill.utils.Edge

trait HelperMethods {

  def makeTweet(
      arrivalSecond: String = "00",
      text: String = "Some text",
      hashtags: List[String] = List[String]()): Tweet = {

    new Tweet(seconds(arrivalSecond), text, makeHashtags(hashtags))
  }

//
//  def makeEdge(hashtag1: Hashtag, hashtag2: Hashtag): Edge = {
//    Edge(hashtag1, hashtag2)
//  }

  private def seconds(s: String): DateTime = {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

    val datePrefix = "2015-06-05 20:00:"
    formatter.parseDateTime(datePrefix + s)
  }

  private def makeHashtags(tags: List[String]): List[Hashtag] = {
    val hashtagList = List[Hashtag]()
    tags.foldLeft(hashtagList)((hl, tag) => Hashtag(tag) +: hl)
  }


}