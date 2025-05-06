Meta:

Narrative:
Activate Product Arrangement service should retrieve Channel ID

Scenario: 1. APALOANS service fetches channelId from pam look up view
Given Contact point id of request is mapped in PAM
When There is call to APALOANS with valid request
Then APALOANS responds

Scenario: 2. APALOANS service throws error for unavailable channelId
Given Contact point id of request is not mapped in PAM
When There is call to APALOANS with valid request
Then APALOANS returns error and throws exception as dataNotAvailable