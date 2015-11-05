package codingchallenge

import codingchallenge.processors.{UnicodeCleaner, TweetProcessor}

class Runner(processors: List[TweetProcessor]) {
  val tweetSource = new TweetSource("data/test.txt")

  def run = {
    tweetSource.validTweets
        .foldLeft(StreamManager(processors))((streamManager, tweet) => streamManager.processTweet(tweet))
        .terminate
  }


}


object Runner {
  val processors = List(UnicodeCleaner(Sink("data/output.txt")))
  val runner = new Runner(processors)

  def run = runner.run
}