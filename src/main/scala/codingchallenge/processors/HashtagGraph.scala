package codingchallenge.processors

import codingchallenge.utils.TimeWindow
import codingchallenge.{Hashtag, Sink, Tweet}

import scala.collection.immutable.HashMap


class HashtagGraph(
    sink: Sink,
    timeWindow: TimeWindow = new TimeWindow(30),
    graphAnalyzer: GraphAnalyzer = GraphAnalyzer()
) extends TweetProcessor {
  def process(tweet: Tweet): HashtagGraph = {

    val newTimeWindow = timeWindow.add(tweet)
    val newGraphAnalyzer = updateHashtagGraph(newTimeWindow)
    val averageDegree: String = "%.2f".format(newGraphAnalyzer.averageDegree) // 2 decimal places

    new HashtagGraph(sink.put(averageDegree), newTimeWindow, newGraphAnalyzer)
  }

  def terminate = sink.persist()

  private def updateHashtagGraph(window: TimeWindow): GraphAnalyzer = {
    val newTweet = window.lastAdded
    val removedTweets = window.lastRemoved

    val prelimGraphAnalyzer: GraphAnalyzer = window.lastAdded match {
      case Some(tweet) => addToGraph(graphAnalyzer, tweet)
      case None => graphAnalyzer
    }

    removedTweets.foldLeft(prelimGraphAnalyzer)((ga, tweet) => removeFromGraph(ga, tweet))
  }

  private def addToGraph(ga: GraphAnalyzer, tweet: Tweet): GraphAnalyzer = {
    val hashtags = extractUniqueHashtags(tweet)
    if (hashtags.size > 1) ga.addHashtagsToGraph(hashtags) else ga
  }

  private def removeFromGraph(ga: GraphAnalyzer, tweet: Tweet): GraphAnalyzer = {
    val hashtags = extractUniqueHashtags(tweet)
    if (hashtags.size > 1) ga.removeHashtagsFromGraph(hashtags) else ga
  }

  private def extractUniqueHashtags(tweet: Tweet): Set[Hashtag] = tweet.hashtags.toSet
}


case class GraphAnalyzer(
    nodeDegrees: HashMap[Hashtag, Long] = HashMap[Hashtag, Long](),
    totalNodes: Int = 0,
    totalDegrees: Long = 0
) {

  lazy val averageDegree: Double = if (totalNodes == 0) 0 else totalDegrees / totalNodes.toDouble

  def addHashtagsToGraph(hashtags: Set[Hashtag]): GraphAnalyzer = {
    val connectionsPerHashtag = hashtags.size - 1
    hashtags.foldLeft(this)((ga, hashtag) => ga.addHashtagConnections(hashtag, connectionsPerHashtag))
  }

  def removeHashtagsFromGraph(hashtags: Set[Hashtag]): GraphAnalyzer = {
    val connectionsPerHashtag = hashtags.size - 1
    hashtags.foldLeft(this)((ga, hashtag) => ga.removeHashtagConnections(hashtag, connectionsPerHashtag))
  }

  private def addHashtagConnections(hashtag: Hashtag, numConnections: Long): GraphAnalyzer = {
    val currentDegree = nodeDegrees.getOrElse(hashtag, 0L)

    if (currentDegree > 0) {
      GraphAnalyzer(updateNodeDegrees(hashtag, currentDegree + numConnections), totalNodes, totalDegrees + numConnections)
    } else {
      GraphAnalyzer(updateNodeDegrees(hashtag, numConnections), totalNodes + 1, totalDegrees + numConnections)
    }
  }

  private def removeHashtagConnections(hashtag: Hashtag, numConnections: Long): GraphAnalyzer = {
    val currentDegree = nodeDegrees.getOrElse(hashtag, -1L)

    if (currentDegree > numConnections) {
      GraphAnalyzer(updateNodeDegrees(hashtag, currentDegree - numConnections), totalNodes, totalDegrees - numConnections)
    } else if (currentDegree == 1) {
      GraphAnalyzer(nodeDegrees - hashtag, totalNodes - 1, totalDegrees - 1)
    } else {
      // hashtag doesn't or shouldn't exist in the hashMap, so removing it won't affect totals
      GraphAnalyzer(nodeDegrees - hashtag, totalNodes, totalDegrees)
    }

  }

  private def updateNodeDegrees(hashtag: Hashtag, newVal: Long): HashMap[Hashtag, Long] = {
    nodeDegrees + (hashtag -> newVal)
  }
}