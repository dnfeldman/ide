package codingchallenge.processors

import codingchallenge.Tweet

trait TweetProcessor {
  def process(tweet: Tweet): TweetProcessor

  def terminate: Unit
}