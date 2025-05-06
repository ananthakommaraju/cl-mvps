Meta:

Narrative:
As the Unauth Sales journey I want to call SOA API Document Manager Service Service to record document meta content so that I can complete activation of the valid application

Scenario: 1. APASA calls SOA API Document Manager Service to record document meta content when application sub status is Customer Details Update Failure(1033)
Given Sub status is Customer Details Update Failure(1033)
And SOA API Document Manager Service call is successful
When UI calls APAD
Then call SOA API Document Manager Service to record document meta content with subStatus 1033