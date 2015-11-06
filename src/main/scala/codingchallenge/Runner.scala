package codingchallenge

import codingchallenge.processors.{HashtagGraph, UnicodeCleaner}

object Runner extends App {
  val processors = List(
    UnicodeCleaner(Sink("tweet_output/ft1.txt")),
    HashtagGraph(Sink("tweet_output/ft2.txt"))
  )

  val tweetSource = new TweetSource("data/tweets.txt")

  tweetSource.validTweets
      .foldLeft(StreamManager(processors))((streamManager, tweet) => streamManager.processTweet(tweet))
      .terminate
}