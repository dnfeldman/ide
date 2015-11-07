package tweetmill

import org.joda.time.format.DateTimeFormat

import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.immutable.Stream.Empty
import scala.util.Try

class Source(filename: String) {
  val tweets = scala.io.Source.fromFile(filename).getLines().toStream

  def validTweets(stream: Stream[String] = tweets): Stream[Tweet] = {
    if (stream.isEmpty) Empty
    else if (extractTweet(stream.head).isSuccess) extractTweet(stream.head).get #:: validTweets(stream.tail)
    else validTweets(stream.tail)
  }

  val timeFormatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy")
  implicit val formats = DefaultFormats // for json4s

  private[this] def extractTweet(line: String): Try[Tweet] = Try(extractingTweetFrom(line))

  private[this] def extractingTweetFrom(line: String): Tweet = {
    val json          = parse(line)

    val createdAtStr  = (json \ "created_at").extract[String]
    val text          = (json \ "text").extract[String]
    val hashtags      = (json \ "entities" \ "hashtags").extract[List[Hashtag]]

    val createdAt = timeFormatter.parseDateTime(createdAtStr)

    Tweet(createdAt, text, hashtags)
  }
}