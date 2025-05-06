Meta:

Narrative:
Activate Product Arrangement service should retrieve Channel ID

Scenario: 1. APACC service fetches channelId from pam look up view
Given Contact point id of request is mapped in PAM and Source System Identifier is not Two
When There is call to APACC with valid request
Then APACC responds

Scenario: 2. APACC service throws error for unavailable channelId
Given Contact point id of request is not mapped in PAM
When There is call to APACC with valid request
Then APACC returns error and throws exception as dataNotAvailable