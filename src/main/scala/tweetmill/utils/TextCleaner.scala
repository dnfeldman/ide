package tweetmill.utils

/* Utility that provides some text cleaning and formatting methods */
trait TextCleaner {
  def removeUnicodeCharacters(text: String): String = {
    text.replaceAll("[^\\p{ASCII}]", "")
  }

  def replaceWhitespaceCharacters(text: String, replacement: String = " "): String = {
    text.replaceAll("[\\s]+", replacement)
  }
}
