Meta:

Narrative:
As the Unauth Sales saving journey I want to APAD to schedule communication - mail and sms for fund remind so that I can complete activation of the valid application

Scenario: 1. APAD schedule communication - mail to customer for fund reminder if application sub status is null and arrangement type is saving (SA)
Given application sub status is null
And arrangement type is saving
When UI calls APAD
Then schedule communication mail for fund reminder

Scenario: 2. APAD schedule communication - sms to customer for fund reminder if application sub status is null and arrangement type is saving (SA) and mobile number is present
!-- Given application sub status is null
!-- And arrangement type is saving
!-- And mobile number is present
!-- When UI calls APAD
!-- Then schedule communication sms for fund reminder