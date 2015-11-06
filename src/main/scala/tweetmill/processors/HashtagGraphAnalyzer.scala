package tweetmill.processors

import tweetmill.utils.{Graph, Edge, TimeWindow}
import tweetmill.{Hashtag, Sink, Tweet}
import org.joda.time.format.DateTimeFormat

import scala.collection.immutable.HashMap


class HashtagGraphAnalyzer(
    sink: Sink,
    timeWindow: TimeWindow,
    hashtagGraph: Graph = Graph()
) extends Processable {
  def process(tweet: Tweet): HashtagGraphAnalyzer = {

    val newTimeWindow = timeWindow.add(tweet)
    val newHashtagGraph = updateHashtagGraph(newTimeWindow)
    val averageDegree: String = "%.2f".format(newHashtagGraph.averageDegree) // 2 decimal places

    new HashtagGraphAnalyzer(sink.put(averageDegree), newTimeWindow, newHashtagGraph)
  }

  def terminate = sink.persist()

  private[this] def updateHashtagGraph(window: TimeWindow): Graph = {
    val newTweet = window.lastAdded
    val removedTweets = window.lastRemoved

    val prelimGraphAnalyzer: Graph = newTweet match {
      case Some(tweet) => addToGraph(hashtagGraph, tweet)
      case None => hashtagGraph
    }

    removedTweets.foldLeft(prelimGraphAnalyzer)((ga, tweet) => removeFromGraph(ga, tweet))
  }

  private[this] def addToGraph(graph: Graph, tweet: Tweet): Graph = {
    val edges = extractHashtagConnections(tweet)
    edges.foldLeft(graph)((g, edge) => g.addEdge(edge))
  }

  private[this] def removeFromGraph(graph: Graph, tweet: Tweet): Graph = {
    val edges = extractHashtagConnections(tweet)
    edges.foldLeft(graph)((g, edge) => g.removeEdge(edge))
  }

  private[this] def extractHashtagConnections(tweet: Tweet): List[Edge] = {
    val hashtags = extractUniqueHashtags(tweet)
    extractEdges(hashtags)
  }

  private[this] def extractUniqueHashtags(tweet: Tweet): List[Hashtag] = tweet.hashtags.toSet.toList
  private[this] def extractEdges(hashtags: List[Hashtag]): List[Edge] = {
    hashtags.combinations(2).map(combo => createEdgeFrom(combo)).toList
  }

  private[this] def createEdgeFrom(list: List[Hashtag]): Edge = Edge(list(0), list(1))
}

object HashtagGraphAnalyzer {
  val windowLengthSeconds = 30
  def apply(sink: Sink): HashtagGraphAnalyzer = new HashtagGraphAnalyzer(sink, timeWindow = TimeWindow(windowLengthSeconds))
//  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
//
//  val datePrefix = "2015-06-05 20:00:"
//
//  val hg = new HashtagGraphAnalyzer(Sink("data/feat2.txt"), timeWindow = new TimeWindow(5))
//
//  def run = {
//    val t0 = new Tweet(formatter.parseDateTime(datePrefix + "00"), "Text", List[Hashtag]())                                               // 0.0
//    val t1 = new Tweet(formatter.parseDateTime(datePrefix + "00"), "Text", List[Hashtag](Hashtag("a"), Hashtag("b")))                     // 1.0
//    val t2 = new Tweet(formatter.parseDateTime(datePrefix + "01"), "Text", List[Hashtag](Hashtag("a")))                                   // 1.0
//    val t3 = new Tweet(formatter.parseDateTime(datePrefix + "02"), "Text", List[Hashtag](Hashtag("b"), Hashtag("A"), Hashtag("c")))       // 2.0
//    val t4 = new Tweet(formatter.parseDateTime(datePrefix + "03"), "Text", List[Hashtag](Hashtag("b"), Hashtag("d")))                     // 2.0
//    val t5 = new Tweet(formatter.parseDateTime(datePrefix + "03"), "Text", List[Hashtag]())                                               // 2.0
//    val t6 = new Tweet(formatter.parseDateTime(datePrefix + "06"), "Text", List[Hashtag](Hashtag("e"), Hashtag("G")))                     //
//    val t7 = new Tweet(formatter.parseDateTime(datePrefix + "20"), "Text", List[Hashtag](Hashtag("e"), Hashtag("A"), Hashtag("c")))       //
//
//    val list = List(t0, t1, t2, t3, t4, t5, t6, t7)
//
//    list.foldLeft(hg)((w, t) => w.process(t)).terminate
//  }
}





