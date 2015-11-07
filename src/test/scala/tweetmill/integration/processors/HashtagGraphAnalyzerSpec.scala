package tweetmill.integration.processors

import org.scalatest.{GivenWhenThen, FunSpec}
import tweetmill.HelperMethods

class HashtagGraphAnalyzerSpec extends FunSpec with GivenWhenThen with HelperMethods {
  describe("HashtagGraphAnalyzer") {
    val tweet0 = makeTweet(arrivalSecond = "00", hashtags = List())
    val tweet1 = makeTweet(arrivalSecond = "01", hashtags = List("a"))
    val tweet2 = makeTweet(arrivalSecond = "02", hashtags = List("a", "B"))
    val tweet3 = makeTweet(arrivalSecond = "03", hashtags = List("b", "C", "a"))
    val tweet4 = makeTweet(arrivalSecond = "04", hashtags = List())
    val tweet5 = makeTweet(arrivalSecond = "07", hashtags = List("e", "g"))
    val tweet6 = makeTweet(arrivalSecond = "09", hashtags = List("b", "g", "a"))
    val tweet7 = makeTweet(arrivalSecond = "20", hashtags = List("f"))

    val tweetStream = List(tweet0, tweet1, tweet2, tweet3, tweet4, tweet5, tweet6, tweet7)
    val hashtagGraphAnalyzer = makeHashtagGraphAnalyzer

    it("generates correct average node degrees") {
      Given("a processed stream of tweets")
      val finalHashtagGraphAnalyzer = tweetStream.foldLeft(hashtagGraphAnalyzer)((hga, tweet) => hga.process(tweet))

      Then("it should contain correct average node degrees")
      val correctNodeDegrees = List(
      "0.00", //tweet0 did not have any hashtags
      "0.00", // tweet1 contained only one hashtag, which is not enough to form an edge
      "1.00", // tweet2 resulted in 2 total nodes and 2 total degrees
      "2.00", // tweet3 added 'b-c' connection, bringing the totals to 3 nodes and 6 total degrees
      "2.00", // tweet4 did not have any hashtags
      "1.60", // tweet5 added 'e-g' connection bringing the totals to 5 nodes and 8 edges
      "2.00", // tweet6 b-g and b-a, but also removed tweet3 and tweet2, so we are left with tweet 5 and 6.
              // Those have 4 nodes and 8 total degrees
      "0.00"   // tweeet7 kicks out other tweets, and having only one hashtag, makes the graph empty
      )

      assert(finalHashtagGraphAnalyzer.sink.contents.reverse == correctNodeDegrees)
    }
  }
}