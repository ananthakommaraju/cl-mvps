Meta:

Narrative:
As the Unauth Sales journey I want to call OCIS API F060 to update marketing preference so that I can complete activation of the valid application

Scenario: 1. APAPCA calls F060 to update marketing preference when application sub status is null
Given Sub status is null
When UI calls APAD
Then call OCIS API F060 to update marketing preference.