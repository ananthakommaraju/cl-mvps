Meta:

Narrative:
As the Unauth Sales saving journey I want to APAD to schedule communication - mail and sms for fund remind so that I can complete activation of the valid application

Scenario: 1. APAD schedule communication - mail to customer for fund reminder if application sub status is null and arrangement type is saving (SA)
Given application sub status is null
When UI calls APAD
Then schedule communication mail for fund reminder