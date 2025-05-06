Meta:

Narrative:
As the Unauth Sales journey I want to APAD to send communication - mail for benefit messages so that I can complete activation of the valid application

Scenario: 1. APAD send communication - benefit mail to customer if application sub status is null and alert messages are enabled
Given application sub status is null
And alert messages are enabled
When UI calls APAD
Then send benefit mail to customer