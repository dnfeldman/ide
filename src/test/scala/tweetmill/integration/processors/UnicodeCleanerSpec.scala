package tweetmill.integration.processors

import org.scalatest.FunSpec
import tweetmill.HelperMethods

class UnicodeCleanerSpec extends FunSpec with HelperMethods{

  describe("HashtagGraphAnalyzer") {

    /* Default timestamp is 2015-06-05 20:00:00 +0000 (Fri) */
    val tweet0 = makeTweet(text = "no unicode")
    val tweet1 = makeTweet(text = "no unicode")
    val tweet2 = makeTweet(text = "some ❤ unicode ❤")
    val tweet3 = makeTweet(text = "юникод")
    val tweet4 = makeTweet(text = "no unicode")

    val tweetStream = List(tweet0, tweet1, tweet2, tweet3, tweet4)
    val unicodeCleaner = makeUnicodeCleaner

    it("removes unicode characters and keeps track of tweets with unicode"){
      val finalState = tweetStream.foldLeft(unicodeCleaner)((uc, tweet) => uc.process(tweet))

      val correctOutputEntries = List(
        "no unicode (timestamp: Fri Jun 05 20:00:00 +0000 2015)",
        "no unicode (timestamp: Fri Jun 05 20:00:00 +0000 2015)",
        "some  unicode  (timestamp: Fri Jun 05 20:00:00 +0000 2015)",
        " (timestamp: Fri Jun 05 20:00:00 +0000 2015)",
        "no unicode (timestamp: Fri Jun 05 20:00:00 +0000 2015)"
      )

      assert(finalState.sink.contents.reverse == correctOutputEntries)
      assert(finalState.tweetsWithUnicode == 2)
    }
  }
}
