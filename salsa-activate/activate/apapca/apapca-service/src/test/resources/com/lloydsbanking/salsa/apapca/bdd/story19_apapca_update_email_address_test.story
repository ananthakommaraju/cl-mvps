Meta:

Narrative:
As the Unauth Sales journey I want to call OCIS API C658 to update email address so that I can complete activation of the valid application

Scenario: 1. APAPCA calls C658 to update email address when application sub status is null
Given Sub status is null
When UI calls APAD
Then call OCIS API C658 to update email address.