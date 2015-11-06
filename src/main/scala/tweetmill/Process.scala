package tweetmill

import tweetmill.processors.{HashtagGraphAnalyzer, UnicodeCleaner}
import scala.collection.mutable.Map

object Process extends App {
  val params = Params.parseArgs(args)

  val processors = List(
    UnicodeCleaner(new Sink(params("unicodeCleanerOut"))),
    HashtagGraphAnalyzer(new Sink(params("hashtagGraphAnalyzerOut")))
  )

  val tweetSource = new Source(params("source"))

  tweetSource.validTweets
      .foldLeft(new StreamManager(processors))((streamManager, tweet) => streamManager.processTweet(tweet))
      .terminate

}

object Params {
  def parseArgs(args: Array[String]): Map[String, String] = {
    args.map(arg => addToParams(arg))

    params
  }

  private val params: Map[String, String] = Map(
    "source" -> "tweet_input/tweets.txt",
    "unicodeCleanerOut" -> "tweet_output/ft1.txt",
    "hashtagGraphAnalyzerOut" -> "tweet_output/ft2.txt"
  )

  private def addToParams(argString: String): Unit = {
    val Array(paramName, paramValue) = argString.split("=")

    if(params.isDefinedAt(paramName.trim)) {
      params.update(paramName, paramValue.trim)
    } else {}
  }
}