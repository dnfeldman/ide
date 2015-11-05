package codingchallenge.utils

import codingchallenge.{Hashtag, Tweet}
import org.joda.time.format.DateTimeFormat

case class TimeWindow(
    lengthInSeconds: Int,
    tweets: List[Tweet] = List[Tweet](),
    lastAdded: Option[Tweet] = None,
    lastRemoved: List[Tweet] = List[Tweet]()) {

  def add(tweet: Tweet): TimeWindow = {
    val prelimWindow = insert(tweet)

    val windowStart = prelimWindow.head.createdAt
    val windowEnd = windowStart.minusSeconds(lengthInSeconds)

    val (finalWindow, droppedTweets) = dropOlderThan(windowEnd, prelimWindow)

    println(windowStart + " to " + windowEnd)
    new TimeWindow(lengthInSeconds, finalWindow, Some(tweet), droppedTweets)
  }

  def dropOlderThan(
      windowEnd: org.joda.time.DateTime,
      prelimWindow: List[Tweet]): (List[Tweet], List[Tweet]) = {

    prelimWindow.partition(tweet => tweet.createdAt.isAfter(windowEnd))
  }

  def insert(tweet: Tweet): List[Tweet] = {
    val insertAt = tweets.indexWhere(t => tweet.timeStamp >= t.timeStamp)

    println(insertAt + " for tweet with t = " + tweet.createdAt)
    if (insertAt == -1) {
      tweets :+ tweet // append to the end
    } else {
      addAtPosition(tweet, insertAt)
    }
  }

  def addAtPosition(tweet: Tweet, pos: Int): List[Tweet] = {
    val (left, right) = tweets.splitAt(pos)

    left ++ (tweet +: right)
  }
}

object TimeWindow {

  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  val datePrefix = "2015-06-05 20:00:"

  def run: TimeWindow = {
    val t1 = new Tweet(formatter.parseDateTime(datePrefix + "00"), "Text", List[Hashtag]())
    val t2 = new Tweet(formatter.parseDateTime(datePrefix + "01"), "Text", List[Hashtag]())
    val t3 = new Tweet(formatter.parseDateTime(datePrefix + "02"), "Text", List[Hashtag]())
    val t4 = new Tweet(formatter.parseDateTime(datePrefix + "03"), "Text", List[Hashtag]())
    val t5 = new Tweet(formatter.parseDateTime(datePrefix + "03"), "Text", List[Hashtag]())
    val t6 = new Tweet(formatter.parseDateTime(datePrefix + "06"), "Text", List[Hashtag]())
    val t7 = new Tweet(formatter.parseDateTime(datePrefix + "20"), "Text", List[Hashtag]())

    val w = new TimeWindow(5)

    val list = List(t1, t2, t3, t4, t5, t6, t7)

    list.foldLeft(w)((w, t) => w.add(t))
  }
}