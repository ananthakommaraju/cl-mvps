Meta:

Narrative:
As the Unauth Sales journey I want to APAD to schedule communication - sms for STP success so that I can complete activation of the valid application

Scenario: 1. APAD schedule communication - sms to customer for STP success if application sub status is null and invoke source id is not online (1)
Given application sub status is null
And source id is not online
And mobile number is present
When UI calls APAD
Then schedule communication sms for STP success