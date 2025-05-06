Meta:

Narrative:
As the Unauth Sales journey I want to call OCIS API C234 to update national insurance number so that I can complete activation of the valid application

Scenario: 1. APASA calls C234 to update national insurance number when application sub status is null
Given Sub status is null
When UI calls APAD
Then call OCIS API C234 to update national insurance number with subStatus null.