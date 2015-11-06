#!/bin/bash
scala -classpath assembly/projectAssembly.jar tweetmill.Process source=tweet_input/tweets.txt unicodeCleanerOut=tweet_output/ft1.txt hashtagGraphAnalyzerOut=tweet_output/ft2.txt
