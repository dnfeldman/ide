package tweetmill.processors

import tweetmill.Tweet

trait Processable {
  def process(tweet: Tweet): Processable

  def terminate: Unit
}