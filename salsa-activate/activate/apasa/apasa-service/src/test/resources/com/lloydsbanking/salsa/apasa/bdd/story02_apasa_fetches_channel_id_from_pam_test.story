Meta:

Narrative:
Activate Product Arrangement service should retrieve Channel ID

Scenario: 1. APASA service fetches channelId from pam look up view
Given Contact point id of request is mapped in PAM and Source System Identifier is not Two
When There is call to APASA with valid request
Then APASA responds

Scenario: 2. APASA service throws error for unavailable channelId
Given Contact point id of request is not mapped in PAM
When There is call to APASA with valid request
Then APASA returns error and throws exception as dataNotAvailable