package tweetmill.unit.utils

import org.scalatest.{GivenWhenThen, FunSpec}
import tweetmill.HelperMethods
import tweetmill.utils.TimeWindow

class TimeWindowSpec extends FunSpec with GivenWhenThen with HelperMethods {

  val tweet0 = makeTweet(arrivalSecond = "00")
  val tweet1 = makeTweet(arrivalSecond = "00")
  val tweet2 = makeTweet(arrivalSecond = "01")
  val tweet3 = makeTweet(arrivalSecond = "02")
  val tweet4 = makeTweet(arrivalSecond = "06")
  
  describe("TimeWindow") {
    val windowLength = 5 // seconds

    it("should allow an empty window to be instantiated"){
      Given("an empty TimeWindow")
      val timeWindow = new TimeWindow(lengthInSeconds = windowLength)

      Then("it should initialize properly")
      assert(timeWindow.tweets.isEmpty)
      assert(timeWindow.lastAdded === None)
      assert(timeWindow.lastRemoved.isEmpty)
    }

    describe("when adding to empty window") {
      it("should allow a tweet to be added") {
        Given("an empty TimeWindow")
        val timeWindow = new TimeWindow(windowLength)

        When("new tweet comes in")
        val newTimeWindow = timeWindow.add(tweet1)

        Then("it properly stores the tweet")
        assert(newTimeWindow.tweets == List(tweet1))

        And("it properly sets lastAdded")
        assert(newTimeWindow.lastAdded === Some(tweet1))

        And("it does not remove any tweets")
        assert(newTimeWindow.lastRemoved.isEmpty)
      }
    }

    describe("when adding to a non-empty window") {
      it("should place latest tweet at the head of tweets list") {
        Given("an non-empty TimeWindow")
        val timeWindow = new TimeWindow(windowLength, tweets=List(tweet2, tweet1))

        When("new latest tweet comes in")
        val newTimeWindow = timeWindow.add(tweet3)

        Then("it properly stores the tweet at the head")
        assert(newTimeWindow.tweets == List(tweet3, tweet2, tweet1))
      }

      it("should place an older tweet at the correct position of tweets list") {
        Given("an non-empty TimeWindow")
        val timeWindow = new TimeWindow(windowLength, tweets=List(tweet3, tweet1))

        When("new older tweet comes in")
        val newTimeWindow = timeWindow.add(tweet2)

        Then("it properly stores the tweet between tweet3 and tweet1")
        assert(newTimeWindow.tweets == List(tweet3, tweet2, tweet1))

        And("it keeps tweet2 as lastAdded")
        assert(newTimeWindow.lastAdded === Some(tweet2))
      }

      it("should remove old tweets if they do not satisfy time window length given a new latest tweet") {
        Given("a non-empty TimeWindow")
        val timeWindow = new TimeWindow(windowLength, tweets=List(tweet2, tweet1, tweet0))

        When("new older tweet comes in")
        val newTimeWindow = timeWindow.add(tweet4)

        Then("it removes tweet1 and tweet0 in the correct order of being inserted")
        assert(newTimeWindow.lastRemoved == List(tweet1, tweet0))

        And("keeps existing tweets that still satisfy the new time frame")
        assert(newTimeWindow.tweets == List(tweet4, tweet2))
        assert(newTimeWindow.lastAdded === Some(tweet4))
      }

      it("should not store a new tweet if it does not satisfy existing time window length") {
        Given("a non-empty TimeWindow")
        val timeWindow = new TimeWindow(windowLength, tweets=List(tweet4))

        When("new older tweet comes in")
        val newTimeWindow = timeWindow.add(tweet0)

        Then("it does not store it")
        assert(newTimeWindow.tweets == List(tweet4))

        And("it removes tweet0 right away")
        assert(newTimeWindow.lastRemoved == List(tweet0))

        And("it maintains reference to last processed tweet")
        assert(newTimeWindow.lastAdded === Some(tweet0))
      }
    }
  }
}
