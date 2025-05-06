Meta:

Narrative:
As the Unauth Sales journey

Scenario: 1. APAPCA  updates Application status as decline (1004) when call send to communication succeeds and asm decision is decline
Given application status is Awaiting Manual ID and V(1007) And refreshed credit decision is decline at ASM
And APAPCA is invoked by OAB
And call to sendCommunication for decline succeeds
When there is a call to APAPCA
Then update application status as decline

Scenario: 2. APAPCA call sendCommunication to send application information to customer and asm decision is refer
Given application status is Awaiting Manual ID and V(1007) And refreshed credit decision is refer at ASM
And APAPCA is invoked by OAB
When there is a call to APAPCA
Then service continues

Scenario: 3. APAPCA updates Application status as approved (1002) when asm decision is approved
Given application status is Awaiting Manual ID and V(1007) And refreshed credit decision is approved at ASM
And APAPCA is invoked by OAB
When there is a call to APAPCA
Then application status as approved


