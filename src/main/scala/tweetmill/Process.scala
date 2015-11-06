package tweetmill

import tweetmill.processors.{HashtagGraphAnalyzer, UnicodeCleaner}

object Process extends App {
  val processors = List(
    UnicodeCleaner(new Sink("tweet_output/ft1.txt")),
    HashtagGraphAnalyzer(new Sink("tweet_output/ft2.txt"))
  )

  val tweetSource = new Source("tweet_input/tweets.txt")

  tweetSource.validTweets
      .foldLeft(new StreamManager(processors))((streamManager, tweet) => streamManager.processTweet(tweet))
      .terminate
}