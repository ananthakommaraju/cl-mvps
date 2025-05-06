Meta:

Narrative:
As the Unauth Sales journey I want to call OCIS API C241 to update party relationship so that I can complete activation of the valid application

Scenario: 1. APASA calls C241 to update party relationship when application sub status is null
Given Sub status is null
When UI calls APAD
Then call OCIS API C241 to update party relationship with subStatus null.