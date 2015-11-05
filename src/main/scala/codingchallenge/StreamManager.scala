package codingchallenge

import processors.TweetProcessor

class StreamManager(processors: List[TweetProcessor]) {
  def processTweet(tweet: Tweet): StreamManager = StreamManager(processors.map(p => p.process(tweet)))

  def terminate: Unit = processors.map(processor => processor.terminate)
}

object StreamManager {
  def apply(tweetProcessors: List[TweetProcessor]): StreamManager = new StreamManager(tweetProcessors)
}
