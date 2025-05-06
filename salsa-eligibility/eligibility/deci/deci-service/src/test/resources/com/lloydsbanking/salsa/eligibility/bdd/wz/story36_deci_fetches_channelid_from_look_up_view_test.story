Narrative:
As a user interface
I want determine eligible customer instruction service fetches channelId
So that I can check the eligibility

Scenario: 1.Determine eligible customer instruction service fetches channelId from pam look up view
Given contact point id of request is mapped in pam
When the UI calls DECI with valid request
Then DECI responds

Scenario: 2. DECI service throws error for unavailable channelId
Given contact point id of request is not  mapped in pam
When the UI calls DECI with valid request
Then DECI returns error and throws exception as dataNotAvailable

