package tweetmill

import processors.Processable

class StreamManager(processors: List[Processable]) {
  def processTweet(tweet: Tweet): StreamManager = sendToEachProcessor(tweet)

  def terminate: Unit = processors.map(processor => processor.terminate)

  private[this] def sendToEachProcessor(tweet: Tweet): StreamManager = {
    new StreamManager(processors.map(p => p.process(tweet)))
  }
}
