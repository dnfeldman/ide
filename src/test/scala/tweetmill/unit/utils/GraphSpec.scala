package tweetmill.unit.utils

import org.scalatest.{GivenWhenThen, FunSpec}
import tweetmill.{Hashtag, HelperMethods}
import tweetmill.utils.{Edge, Graph}

import scala.collection.immutable.HashMap

class GraphSpec extends FunSpec with GivenWhenThen with HelperMethods {

  describe("Graph") {

    it("should allow an empty graph to be instantiated"){
      Given("an empty graph")
      val graph = new Graph()

      Then("it should initialize properly")
      assert(graph.edgeCounts.isEmpty)
      assert(graph.nodeDegrees.isEmpty)
      assert(graph.totalNodes == 0L)
      assert(graph.totalDegrees == 0L)
      assert(graph.averageDegree == 0.0)
    }

    describe("When adding an Edge to an empty graph") {
      val tag1 = Hashtag("a")
      val tag2 = Hashtag("b")
      val edge = Edge(tag1, tag2)

      it("returns correct graph") {
        Given("an empty graph")
        val graph = new Graph

        Then("it should update correctly")
        val newGraph = graph.addEdge(edge)

        assert(newGraph.edgeCounts == HashMap(edge -> 1L))
        assert(newGraph.nodeDegrees == HashMap(tag1 -> 1L, tag2 -> 1L))
        assert(newGraph.totalNodes == 2L)
        assert(newGraph.totalDegrees == 2L)
        assert(newGraph.averageDegree == 1.0)
      }
    }

    describe("When removing an Edge from an empty graph") {
      val tag1 = Hashtag("a")
      val tag2 = Hashtag("b")
      val edge = Edge(tag1, tag2)

      it("returns empty graph") {
        Given("an empty graph")
        val graph = new Graph

        Then("it should not update anything")
        val newGraph = graph.removeEdge(edge)

        assert(newGraph.edgeCounts.isEmpty)
        assert(newGraph.nodeDegrees.isEmpty)
        assert(newGraph.totalNodes == 0L)
        assert(newGraph.totalDegrees == 0L)
        assert(newGraph.averageDegree == 0.0)
      }
    }

    describe("When adding an Edge to an non-empty graph") {
      val tagA = Hashtag("A")
      val tagB = Hashtag("B")
      val tagC = Hashtag("C")
      val tagD = Hashtag("D")

      val edge1 = Edge(tagA, tagB)
      val edge2 = Edge(tagC, tagD)
      val edge3 = Edge(tagB, tagC)

      val graph = Graph().addEdge(edge1)

      describe("When an edge already exists") {
        Given("adding an existing edge")
        val newGraph = graph.addEdge(edge1)

        Then("it should update edgeCounts correctly")
        assert(newGraph.edgeCounts == HashMap(edge1 -> 2L))

        And("it should not change nodeDegrees")
        assert(newGraph.nodeDegrees == HashMap(tagA -> 1L, tagB -> 1L))

        And("it should not change total nodes")
        assert(newGraph.totalNodes == 2L)

        And("it should not change total degrees")
        assert(newGraph.totalDegrees == 2L)

        And("it should not change average degree")
        assert(newGraph.averageDegree == 1.0)
      }

      describe("When an edge does not share connections with existing nodes") {
        Given("adding an non-existing edge")
        val newGraph = graph.addEdge(edge2)

        Then("it should update edgeCounts correctly")
        assert(newGraph.edgeCounts == HashMap(edge1 -> 1L, edge2 -> 1L))

        And("it should change nodeDegrees")
        assert(newGraph.nodeDegrees == HashMap(tagA -> 1L, tagB -> 1L, tagC -> 1L, tagD -> 1L))

        And("it should change total nodes")
        assert(newGraph.totalNodes == 4L)

        And("it should change total degrees")
        assert(newGraph.totalDegrees == 4L)

        And("it should not change average degree")
        assert(newGraph.averageDegree == 1.0)
      }

      describe("when an edge contains an already existing node") {
        Given("adding an edge with existing node")
        val newGraph = graph.addEdge(edge3)

        Then("it should update edgeCounts correctly")
        assert(newGraph.edgeCounts == HashMap(edge1 -> 1L, edge3 -> 1L))

        And("it should change nodeDegrees")
        assert(newGraph.nodeDegrees == HashMap(tagA -> 1L, tagB -> 2L, tagC -> 1L))

        And("it should change total nodes")
        assert(newGraph.totalNodes == 3L)

        And("it should change total degrees")
        assert(newGraph.totalDegrees == 4L)

        And("it should not change average degree")
        assert(newGraph.averageDegree == 4/3.0)
      }
    }

    describe("When removing an Edge from a non-empty graph") {
      val tagA = Hashtag("A")
      val tagB = Hashtag("B")
      val tagC = Hashtag("C")
      val tagD = Hashtag("D")

      val edge1 = Edge(tagA, tagB)
      val edge2 = Edge(tagC, tagD)
      val edge3 = Edge(tagB, tagC)

      describe("When an edge already exists") {
        val graph = Graph().addEdge(edge1).addEdge(edge1)

        Given("removing an existing edge")
        val newGraph = graph.removeEdge(edge1)

        Then("it should update edgeCounts correctly")
        assert(newGraph.edgeCounts == HashMap(edge1 -> 1L))

        And("it should not change nodeDegrees")
        assert(newGraph.nodeDegrees == HashMap(tagA -> 1L, tagB -> 1L))

        And("it should not change total nodes")
        assert(newGraph.totalNodes == 2L)

        And("it should not change total degrees")
        assert(newGraph.totalDegrees == 2L)

        And("it should not change average degree")
        assert(newGraph.averageDegree == 1.0)
      }

      describe("When an edge does not share connections with existing nodes") {
        val graph = Graph().addEdge(edge1).addEdge(edge2)

        Given("removing a existing edge")
        val newGraph = graph.removeEdge(edge2)

        Then("it should have no reference to removed edge")
        assert(newGraph.edgeCounts == HashMap(edge1 -> 1L))

        And("it should have no reference to removed nodes")
        assert(newGraph.nodeDegrees == HashMap(tagA -> 1L, tagB -> 1L))

        And("it should change total nodes")
        assert(newGraph.totalNodes == 2L)

        And("it should change total degrees")
        assert(newGraph.totalDegrees == 2L)

        And("it should not change average degree")
        assert(newGraph.averageDegree == 1.0)
      }

      describe("when an edge contains an already existing node") {
        val graph = Graph().addEdge(edge1).addEdge(edge3)

        Given("removing last edge containing reference to a node")
        val newGraph = graph.removeEdge(edge3)

        Then("it should have no reference to removed edge")
        assert(newGraph.edgeCounts == HashMap(edge1 -> 1L))

        And("it should change nodeDegrees")
        assert(newGraph.nodeDegrees == HashMap(tagA -> 1L, tagB -> 1L))

        And("it should change total nodes")
        assert(newGraph.totalNodes == 2L)

        And("it should change total degrees")
        assert(newGraph.totalDegrees == 2L)

        And("it should change average degree")
        assert(graph.averageDegree == 4/3.0)
        assert(newGraph.averageDegree == 1.0)
      }
    }
  }
}
