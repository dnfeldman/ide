package tweetmill.integration

import org.joda.time.format.DateTimeFormat
import org.scalatest.{GivenWhenThen, FunSpec}
import tweetmill.{Hashtag, Source, HelperMethods}

class SourceSpec extends FunSpec with GivenWhenThen with HelperMethods {

  describe("Source") {
    val filename = "src/test/scala/tweetmill/integration/files/test_input.txt"
    val formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withZoneUTC()

    it("should process input strings into tweets") {
      When("given a source of tweets")
      val source = new Source(filename)
      val validTweets = source.validTweets().toList // make a list instead of iterator to make it reusable

      Then("ignores malformed lines that cannot be converted into a Tweet")

      assert(scala.io.Source.fromFile(filename).getLines.size == 3)
      assert(validTweets.length == 2)

      And("parses valid tweets correctly")
      val List(tweet1, tweet2) = validTweets

      assert(tweet1.createdAt.toString(formatter) == "Fri Oct 30 15:29:44 +0000 2015")
      assert(tweet1.text == "some text")
      assert(tweet1.hashtags == List(Hashtag("A"), Hashtag("B")))

      assert(tweet2.createdAt.toString(formatter) == "Fri Oct 30 15:29:47 +0000 2015")
      assert(tweet2.text == "some other text")
      assert(tweet2.hashtags == List())
    }
  }
}
