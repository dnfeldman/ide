package codingchallenge

case class Tweet(createdAt: org.joda.time.DateTime, text: String, hashtags: List[Hashtag]) {
  val timeStamp: Long = createdAt.getMillis()
}

case class Hashtag(text: String) {
  override def equals(other: Any) = other match {
    case that: Hashtag => that.text.equalsIgnoreCase(this.text)
    case _ => false
  }

  override def hashCode = text.toLowerCase.hashCode
}

object RandomTweet {

  def apply(): Tweet = {
    new Tweet(org.joda.time.DateTime.now(), "Text", List[Hashtag]())
  }
}