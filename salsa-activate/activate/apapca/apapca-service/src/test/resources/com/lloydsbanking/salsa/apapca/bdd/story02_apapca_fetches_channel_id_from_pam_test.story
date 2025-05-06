Meta:

Narrative:
Activate Product Arrangement service should retrieve Channel ID

Scenario: 1. APAPCA service fetches channelId from pam look up view
Given Contact point id of request is mapped in PAM and Source System Identifier is not Two
And sort code is present in request
When There is call to APAPCA with valid request when sort code present
Then APAPCA responds

Scenario: 2. APAPCA service throws error for unavailable channelId
Given Contact point id of request is not mapped in PAM
When There is call to APAPCA with valid request
Then APAPCA returns error and throws exception as dataNotAvailable