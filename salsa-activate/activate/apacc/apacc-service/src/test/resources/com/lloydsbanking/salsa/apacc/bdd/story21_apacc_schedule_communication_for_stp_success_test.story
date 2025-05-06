Meta:

Narrative:
As the Unauth Sales journey I want to APAD to schedule communication - sms for STP success so that I can complete activation of the valid application

Scenario: 1. APACC schedule communication - sms to customer for STP success if mobile number is present
Given source id is not online
And mobile number is present
When there is a call to APACC
Then schedule communication sms for STP success with template STPCCRSUCCESS