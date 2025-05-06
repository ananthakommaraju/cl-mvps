Meta:

Narrative:
As a Unauth Sales Journey I want to check OAP journey so that complete the activation of valid application


Scenario: 1. APACC updates Application status as decline (1004) when call send to communication succeeds and asm decision is decline and invoke mode is OAP
Given refreshed credit decision is decline at ASM and application status is awaiting manual IDV
And APACC is invoked by galaxy OAP
And call to sendCommunication for decline succeeds
When there is a call to APACC
Then update application status as decline


Scenario: 3. APACC updates Application status as approved (1002) when asm decision is approved and invoke mode is OAP
Given refreshed credit decision is approved at ASM and application status is awaiting manual IDV
And APACC is invoked by galaxy OAP
When there is a call to APACC
Then set credit limit and application status as approved