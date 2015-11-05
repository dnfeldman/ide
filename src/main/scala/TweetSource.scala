import java.lang.RuntimeException

import scala.annotation.tailrec








//class TweetProcessor(counter: Counter = new Counter()) {
//
//  def processTweet(tweet: Tweet): TweetProcessor = new TweetProcessor(counter.processTweet(tweet))
//
//  def terminate: Unit = {
//    counter.terminate
//  }
//}
//
//object TweetProcessor {
//  def apply(): TweetProcessor = {
//    new TweetProcessor(new Counter)
//  }
//}
//class TweetProcessor(source: JSONSource) {
//
//  @tailrec
//  final def processTweetStream(stream: Stream[Tweet], acc: List[String]) {
//    if (!stream.isEmpty) { processTweetStream(stream.tail, stream.head :: acc) }
//    else { println(acc) }
//  }
//}

//object JSONSource {
//  val source = new JSONSource("data/tweets.txt")
//  val sink = new Sink("data/output.txt")
//  = new JSONSource("data/tweets.txt")

  //  val tweets = TweetCollection(source.tweets)
//
//  def run: Unit = sink.put(tweets)

//}


//case class Tweet(createdAt: java.util.Date, text: String, hashtags: List[Hashtag]) {
//  def removeUnicode: String = text.replaceAll("[^\\p{ASCII}]|\n", "")
//
//  def mkString: String = cleanText + " (timestamp: " + createdAt + ")"
//
//  val cleanText: String = removeUnicode
//  val hasUnicode: Boolean = if (cleanText.length() == text.length()) true else false
//  val emptyString = ""
//
//}
//
//
//case class TweetCollection(tweets: List[Tweet]) {
//  val tweetsWithUnicodeCount = tweets.foldLeft(0) {
//    (tweetsWithUnicode, tweet) => tweetsWithUnicode + (if (tweet.hasUnicode) 1 else 0)
//  }
//
//  def mkString: String = tweets.map(tweet => tweet.mkString).mkString("\n") + "\n\n" + footer
//
//  def footer: String = tweetsWithUnicodeCount + " tweets contained unicode.\n"
//}


//case class TweetHashtag(key: Int, hashtags: Set[Int])
//case class Window(tweetHashtags: List[TweetHashtag] = List[TweetHashtag]()) {
//
//  def add(tweetHashtag: TweetHashtag): Window = {
//    val prelimWindow = insert(tweetHashtag)
//
//    val maxKey = prelimWindow.head.key - 2
//    val finalWindow = dropOlderThan(maxKey, prelimWindow.reverse).reverse
//    Window(finalWindow)
//  }
//
//  def insert(tweetHashtag: TweetHashtag): List[TweetHashtag] = {
//    val insertAt = tweetHashtags.indexWhere(th => tweetHashtag.key >= th.key)
//
//    if(insertAt == -1) {
//      tweetHashtags :+ tweetHashtag // append to the end
//    } else {
//      addAtPosition(tweetHashtag, insertAt)
//    }
//  }
//
//  def addAtPosition(tweetHashtag: TweetHashtag, pos: Int): List[TweetHashtag] = {
//    val (left, right) = tweetHashtags.splitAt(pos)
//
//    if (right.head.key == tweetHashtag.key) {
//      left ++ (TweetHashtag(tweetHashtag.key, tweetHashtag.hashtags ++ right.head.hashtags) +: right.tail)
//    } else {
//      left ++ (tweetHashtag +: right)
//    }
//  }
//
//  def dropOlderThan(maxKey: Int, reversedWindow: List[TweetHashtag]): List[TweetHashtag] = {
//    if (reversedWindow.head.key <= maxKey) {
//      processTweetHashtag(reversedWindow.head)
//      dropOlderThan(maxKey, reversedWindow.tail)
//    } else {
//      reversedWindow
//    }
//  }
//
//  def processTweetHashtag(tweetHashtag: TweetHashtag): Unit = {
//    println("Droppig " + tweetHashtag.key)
//  }
//
//  // val a =  insert(TweetHashtag(1, Set(3,4)), insert(TweetHashtag(2, Set(2,3)), insert(TweetHashtag(1, Set(1,2,3)))))
//  // val b = insert(TweetHashtag(1, Set(4,5)), a)
// // val c = insert(TweetHashtag(4, Set(1,2)), b)
//  // val d = insert(TweetHashtag(2, Set()), c)
//  // val e = insert(TweetHashtag(3, Set(3,4,5)), d)
//  // val f = insert(TweetHashtag(0, Set(1)), e)
//
//  // val b = List(TweetHashtag(6, Set(1,2)), TweetHashtag(2, Set(2,3)), TweetHashtag(1, Set(3,4)))
//}