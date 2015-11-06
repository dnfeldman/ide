package codingchallenge.processors

import codingchallenge.utils.TimeWindow
import codingchallenge.{Hashtag, Sink, Tweet}
import org.joda.time.format.DateTimeFormat

import scala.collection.immutable.HashMap


class HashtagGraph(
    sink: Sink,
    timeWindow: TimeWindow = new TimeWindow(30),
    hashtagGraph: Graph = Graph()
) extends TweetProcessor {
  def process(tweet: Tweet): HashtagGraph = {

    val newTimeWindow = timeWindow.add(tweet)
    val newHashtagGraph = updateHashtagGraph(newTimeWindow)
    val averageDegree: String = "%.2f".format(newHashtagGraph.averageDegree) // 2 decimal places

    new HashtagGraph(sink.put(averageDegree), newTimeWindow, newHashtagGraph)
  }

  def terminate = sink.persist()

  private def updateHashtagGraph(window: TimeWindow): Graph = {
    val newTweet = window.lastAdded
    val removedTweets = window.lastRemoved

    val prelimGraphAnalyzer: Graph = newTweet match {
      case Some(tweet) => addToGraph(hashtagGraph, tweet)
      case None => hashtagGraph
    }

    removedTweets.foldLeft(prelimGraphAnalyzer)((ga, tweet) => removeFromGraph(ga, tweet))
  }

  private def addToGraph(graph: Graph, tweet: Tweet): Graph = {
    val edges = extractHashtagConnections(tweet)
    edges.foldLeft(graph)((g, edge) => g.addEdge(edge))
  }

  private def removeFromGraph(graph: Graph, tweet: Tweet): Graph = {
    val edges = extractHashtagConnections(tweet)
    edges.foldLeft(graph)((g, edge) => g.removeEdge(edge))
  }

  private def extractHashtagConnections(tweet: Tweet): List[Edge] = {
    val hashtags = extractUniqueHashtags(tweet)
    extractEdges(hashtags)
  }

  private def extractUniqueHashtags(tweet: Tweet): List[Hashtag] = tweet.hashtags.toSet.toList
  private def extractEdges(hashtags: List[Hashtag]): List[Edge] = {
    hashtags.combinations(2).map(combo => createEdgeFrom(combo)).toList
  }

  private def createEdgeFrom(list: List[Hashtag]): Edge = Edge(list(0), list(1))
}

object HashtagGraph {
  val windowLengthSeconds = 30
  def apply(sink: Sink): HashtagGraph = new HashtagGraph(sink, timeWindow = TimeWindow(windowLengthSeconds))
//  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
//
//  val datePrefix = "2015-06-05 20:00:"
//
//  val hg = new HashtagGraph(Sink("data/feat2.txt"), timeWindow = new TimeWindow(5))
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



case class Edge(node1: Hashtag, node2: Hashtag) {
  assert(node1 != node2, "Cannot create an edge pointing to the same node")

  val nodes: Set[Hashtag] = Set(node1, node2)

  override def equals(other: Any) = other match {
    case that: Edge => that.nodes == nodes
    case _ => false
  }

  override def hashCode = nodes.hashCode
}

case class Graph(
    edgeCounts: HashMap[Edge, Long] = HashMap[Edge, Long](),
    nodeDegrees: HashMap[Hashtag, Long] = HashMap[Hashtag, Long](),
    totalNodes: Long = 0L,
    totalDegrees: Long = 0L) {

  val averageDegree: Double = if (totalNodes == 0) 0 else totalDegrees / totalNodes.toDouble

  def addEdge(edge: Edge): Graph = {
    val newEdgeCounts: HashMap[Edge, Long] = incrementCounter[Edge](edgeCounts, edge)
    val newNodeDegrees: HashMap[Hashtag, Long] = needToUpdateNodeDegrees(edge) match {
      case true => edge.nodes.foldLeft(nodeDegrees)((nd, hashtag) => incrementCounter[Hashtag](nd, hashtag))
      case false => nodeDegrees
    }

    val newTotalNodes: Long = totalNodes +
        (if(nodeDegrees.isDefinedAt(edge.node1)) 0L else 1L) +
        (if(nodeDegrees.isDefinedAt(edge.node2)) 0L else 1L)

    val newTotalDegrees: Long = totalDegrees + (if (justAdded(newEdgeCounts, edge)) 2L else 0L)

    Graph(newEdgeCounts, newNodeDegrees, newTotalNodes, newTotalDegrees)
  }

  def removeEdge(edge: Edge): Graph = {
    val newEdgeCounts: HashMap[Edge, Long] = decrementCounter[Edge](edgeCounts, edge)
    val newNodeDegrees: HashMap[Hashtag, Long] = newEdgeCounts.isDefinedAt(edge) match {
      case true => nodeDegrees // edge still there, no changes to node degrees
      case false => edge.nodes.foldLeft(nodeDegrees)((nd, hashtag) => decrementCounter[Hashtag](nd, hashtag))
    }

    val newTotalNodes: Long = totalNodes -
        (if(newNodeDegrees.isDefinedAt(edge.node1)) 0L else 1L) -
        (if(newNodeDegrees.isDefinedAt(edge.node2)) 0L else 1L)

    val newTotalDegrees: Long = totalDegrees - (if (justRemoved(newEdgeCounts, edge)) 2L else 0L)

    Graph(newEdgeCounts, newNodeDegrees, newTotalNodes, newTotalDegrees)
  }


  private def incrementCounter[T](hashMap: HashMap[T, Long], key: T): HashMap[T, Long] = {
    hashMap + (key -> (hashMap.getOrElse(key, 0L) + 1L))
  }

  private def decrementCounter[T](hashMap: HashMap[T, Long], key: T): HashMap[T, Long] = {
    val currentCount = hashMap.getOrElse(key, -1L)
    if (currentCount > 1) hashMap + (key -> (currentCount - 1L)) else hashMap - key
  }

  private def needToUpdateNodeDegrees(newEdge: Edge): Boolean = !edgeCounts.isDefinedAt(newEdge)

  private def justAdded(newEdgeCounts: HashMap[Edge, Long], edge: Edge): Boolean = {
    !edgeCounts.isDefinedAt(edge) && newEdgeCounts.isDefinedAt(edge) && newEdgeCounts(edge) == 1L
  }

  private def justRemoved(newEdgeCounts: HashMap[Edge, Long], edge: Edge): Boolean = {
    edgeCounts.isDefinedAt(edge) && edgeCounts(edge) == 1L && !newEdgeCounts.isDefinedAt(edge)
  }


}