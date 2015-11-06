package codingchallenge.utils

import codingchallenge.Tweet
import org.joda.time.DateTime

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

    new TimeWindow(lengthInSeconds, finalWindow, Some(tweet), droppedTweets)
  }

  def dropOlderThan(
      windowEnd: DateTime,
      prelimWindow: List[Tweet]): (List[Tweet], List[Tweet]) = {

    prelimWindow.partition(tweet => tweet.createdAt.isAfter(windowEnd))
  }

  def insert(tweet: Tweet): List[Tweet] = {
    val insertAt = tweets.indexWhere(t => tweet.timeStamp >= t.timeStamp)

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