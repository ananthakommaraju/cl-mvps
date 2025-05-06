Meta:

Narrative:
As the Unauth Sales journey I want to call SOA API Document Manager Service Service to record document meta content so that I can complete activation of the valid application

Scenario: 1. APAPCA calls SOA API Document Manager Service to record document meta content when application sub status is null
Given Sub status is null
When UI calls APAD
Then call SOA API Document Manager Service to record document meta content