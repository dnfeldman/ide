package tweetmill.utils

import tweetmill.Hashtag

import scala.collection.immutable.HashMap

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

    val newTotalNodes: Long = if(totalNodes == 0) { 0 } else {
      totalNodes -
        (if (newNodeDegrees.isDefinedAt(edge.node1)) 0L else 1L) -
        (if (newNodeDegrees.isDefinedAt(edge.node2)) 0L else 1L) }

    val newTotalDegrees: Long = if(totalDegrees == 0) { 0 } else {
      totalDegrees - (if (justRemoved(newEdgeCounts, edge)) 2L else 0L)
    }

    Graph(newEdgeCounts, newNodeDegrees, newTotalNodes, newTotalDegrees)
  }


  private[this] def incrementCounter[T](hashMap: HashMap[T, Long], key: T): HashMap[T, Long] = {
    hashMap + (key -> (hashMap.getOrElse(key, 0L) + 1L))
  }

  private[this] def decrementCounter[T](hashMap: HashMap[T, Long], key: T): HashMap[T, Long] = {
    val currentCount = hashMap.getOrElse(key, -1L)
    if (currentCount > 1) hashMap + (key -> (currentCount - 1L)) else hashMap - key
  }

  private[this] def needToUpdateNodeDegrees(newEdge: Edge): Boolean = !edgeCounts.isDefinedAt(newEdge)

  private[this] def justAdded(newEdgeCounts: HashMap[Edge, Long], edge: Edge): Boolean = {
    !edgeCounts.isDefinedAt(edge) && newEdgeCounts.isDefinedAt(edge) && newEdgeCounts(edge) == 1L
  }

  private[this] def justRemoved(newEdgeCounts: HashMap[Edge, Long], edge: Edge): Boolean = {
    edgeCounts.isDefinedAt(edge) && edgeCounts(edge) == 1L && !newEdgeCounts.isDefinedAt(edge)
  }
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
