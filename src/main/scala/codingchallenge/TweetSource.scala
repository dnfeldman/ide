package codingchallenge

import org.joda.time.format.DateTimeFormat

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Try

class TweetSource(filename: String) {
  val tweets = Source.fromFile(filename).getLines().map(line => extractTweet(line))
  val validTweets = tweets.collect { case tweetExtract: Try[Tweet] if tweetExtract.isSuccess => tweetExtract.get }

  val timeFormatter = DateTimeFormat.forPattern("EEE MMM dd H:m:s Z yyyy")
  implicit val formats = DefaultFormats // for json4s

  private def extractTweet(line: String): Try[Tweet] = Try(extractingTweetFrom(line))

  private def extractingTweetFrom(line: String): Tweet = {
    val json          = parse(line)

    val createdAtStr  = (json \ "created_at").extract[String]
    val text          = (json \ "text").extract[String]
    val hashtags      = (json \ "entities" \ "hashtags").extract[List[Hashtag]]

    val createdAt = timeFormatter.parseDateTime(createdAtStr)

    Tweet(createdAt, text, hashtags)
  }
}
