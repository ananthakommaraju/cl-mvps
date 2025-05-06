Meta:

Narrative:
As the Unauth Sales journey I want to call SOA API create service arrangement to update life style benefit so that I can complete activation of the valid application

Scenario: 1. APASA calls SOA API create service arrangement to update life style benefit when application sub status is null
Given Sub status is null
When UI calls APAD
Then call SOA API create service arrangement to update life style benefit with subStatus null.