Meta:

Narrative:
As the Unauth Sales journey I want to call SOA API Involved Party Management Service to record customer details so that I can complete activation of the valid application

Scenario: 1. APAPCA calls SOA API Involved Party Management Service to record customer details when application sub status is null
Given Sub status is null
When UI calls APAD
Then call SOA API Involved Party Management Service to record customer details.