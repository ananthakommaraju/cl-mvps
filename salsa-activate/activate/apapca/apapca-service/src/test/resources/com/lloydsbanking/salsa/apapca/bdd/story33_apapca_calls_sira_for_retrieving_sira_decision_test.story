Meta:

Narrative:
As the Unauth Sales journey I want to call SIRA API to update total rule score so that I can complete activation of the valid application

Scenario: 1. APAPCA calls Sira to update total rule score when application sub status is null
Given Sub status is null
When UI calls APAD
Then call OCIS API SIRA API to update total rule score.