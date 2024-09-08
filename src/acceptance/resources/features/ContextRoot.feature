Feature: Context Root of this API
  In order to use Bucket data API, it must be available

  Scenario: ContextRoot on list
    Given the bucket application is alive
    When I navigate to "https://bucket.data.trevorism.com"
    Then then a link to the help page is displayed

  Scenario: Ping on list
    Given the bucket application is alive
    When I ping the application deployed to "https://bucket.data.trevorism.com"
    Then pong is returned, to indicate the service is alive


