package tweetmill.processors

import tweetmill.utils.{TextCleaner, Graph, Edge, TimeWindow}
import tweetmill.{Hashtag, Sink, Tweet}

/** An entity that maintains the current state of hashtag connections graph from tweets within some time
  * window as well as well as provide ability to update the graph by processing the next tweet from a stream
  * @constructor creates new HashtagGraphAnalyzer with an instance of a sSnk, a TimeWindow, and a Graoh
  * @param sink where current state can be written to
  * @param timeWindow current TimeWindow containing all tweets that fall within that time frame
  * @param hashtagGraph current Graph of hashtags connections from tweets that fall within the time frame
  */
class HashtagGraphAnalyzer(
    val sink: Sink,
    timeWindow: TimeWindow,
    hashtagGraph: Graph = Graph()
) extends Processable with TextCleaner {

  /** Prcesses new tweet and updates hashtag graph accordingly
    * @param tweet new tweet to process
    * @return a new HashtagGraphAnalyzer instance that contains updated sink, timeWindow, and graph after processing the tweet
    */
  def process(tweet: Tweet): HashtagGraphAnalyzer = {
    val newTimeWindow = timeWindow.add(tweet)
    val newHashtagGraph = updateHashtagGraph(newTimeWindow)
    val averageDegree: String = "%.2f".format(newHashtagGraph.averageDegree) // 2 decimal places

    new HashtagGraphAnalyzer(sink.put(averageDegree), newTimeWindow, newHashtagGraph)
  }

  def terminate = sink.persist()

  /** Given an instance of TimeWindow, generates a Graph of hashtag connections from all the tweets in the window
    *
    * @param window an instance of TimeWindow
    * @return new instance of Graph containing updated hashtag connections
    */
  private[this] def updateHashtagGraph(window: TimeWindow): Graph = {
    val newTweet = window.lastAdded
    val removedTweets = window.lastRemoved

    val prelimGraphAnalyzer: Graph = newTweet match {
      case Some(tweet) => addToGraph(hashtagGraph, tweet)
      case None => hashtagGraph
    }

    removedTweets.foldLeft(prelimGraphAnalyzer)((ga, tweet) => removeFromGraph(ga, tweet))
  }

  /** Adds a tweet to the graph of hashtag connections.
    *
    * @param graph graph to append a tweet to
    * @param tweet tweet to append to graph
    * @return a new instance of Graph with tweet's hashtags added
    */
  private[this] def addToGraph(graph: Graph, tweet: Tweet): Graph = {
    val edges = extractHashtagConnections(tweet)
    edges.foldLeft(graph)((g, edge) => g.addEdge(edge))
  }

  /** Removes a tweet from the graph of hashtag connections (e.g. if does not satisfy time window constraint
    *
    * @param graph graph to remove a tweet from
    * @param tweet tweet to remove from graph
    * @return a new instance of Graph with tweet's hashtags removed
    */
  private[this] def removeFromGraph(graph: Graph, tweet: Tweet): Graph = {
    val edges = extractHashtagConnections(tweet)
    edges.foldLeft(graph)((g, edge) => g.removeEdge(edge))
  }

  private[this] def extractHashtagConnections(tweet: Tweet): List[Edge] = {
    val hashtags = extractUniqueHashtags(tweet)
    extractEdges(hashtags)
  }

  private[this] def extractUniqueHashtags(tweet: Tweet): List[Hashtag] = {
    tweet.hashtags.map(hashtag => cleanHashtags(hashtag)).distinct
  }

  private[this] def cleanHashtags(hashtag: Hashtag): Hashtag = {
    Hashtag(removeUnicodeCharacters(hashtag.text))
  }

  /** Given a list of hashtags, builds edges reprenting hashtags' connections. It will generate an edge
    * for any 2 combinations of hashtags. For example, a list of hashtags A, B, and C will result in
    * a list of 3 edges: A-B, A-C, B-C. Note that if a list of hashtags contains fewer than 2 elements,
    * this will return an empty List
    *
    * @param hashtags a list of hashtags to build connections for
    * @return a list of edges connecting provided hashtags. Will be empty unless hashtags.length > 1
    */
  private[this] def extractEdges(hashtags: List[Hashtag]): List[Edge] = {
    hashtags.combinations(2).map(combo => createEdgeFrom(combo)).toList
  }

  private[this] def createEdgeFrom(list: List[Hashtag]): Edge = Edge(list(0), list(1))
}

object HashtagGraphAnalyzer {
  val windowLengthSeconds = 60
  def apply(sink: Sink): HashtagGraphAnalyzer = new HashtagGraphAnalyzer(sink, timeWindow = TimeWindow(windowLengthSeconds))
}





