Meta:

Narrative:
As a Unauth Sales Journey I want to check asm decision so that complete the activation of valid application

Scenario: 1. APAPCA calls ASM API F425 to retrieve updated ASM decision with credit source system as 012 when ASM decision with source system 025 is not decline
Given credit decision by ASM with credit score source system as 025 is not decline
When there is a call to APAPCA
Then call ASM API F425 to retrieve updated ASM decision with credit score source system as 012

Scenario: 2. APAPCA call sendCommunication to send application information to customer when invoke mode is offline (4) and asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to sendCommunication for referred succeeds
When there is a call to APAPCA
Then service continues

Scenario: 3. APAPCA sendCommunication call returns error and service continues when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to sendCommunication for referred gives error
When there is a call to APAPCA
Then service continues

Scenario: 4. APAPCA call retrieveLookupValues with REFERRAL_TEAM_GROUPS when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds
When there is a call to APAPCA
Then service continues

Scenario: 5. APAPCA call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001 when retrieveLookupValues with REFERRAL_TEAM_GROUPS fails
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS fails
When there is a call to APAPCA
Then call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001

Scenario: 6. APAPCA call retrieve referral team details with success when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to retrieveReferralTeamDetails succeeds
When there is a call to APAPCA
Then service continues

Scenario: 7. APAPCA task creation call succeeds and service assign application status and referral code in response when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to task creation succeeds
When there is a call to APAPCA
Then service set referral details in response

Scenario: 8. APAPCA task creation call fails and service assign application status and extra conditions in response when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCA is invoked by galaxy offline
And call to task creation fails
When there is a call to APAPCA
Then service set extra conditions in response

Scenario: 9. APAPCA updates application status as decline (1004) when asm decision is decline and invoke mode is online (1)
Given refreshed credit decision is decline at ASM
And APAPCA is invoked by galaxy online
When there is a call to APAPCA
Then update application status as decline

Scenario: 10. APAPCA updates Application status as decline (1004) when call send to communication succeeds and asm decision is decline and invoke mode is not online
Given refreshed credit decision is decline at ASM
And APAPCA is invoked by galaxy offline
And call to sendCommunication for decline succeeds
When there is a call to APAPCA
Then update application status as decline

Scenario: 11. APAPCA updates Application status as decline (1004) when send communication fails and asm decision is decline and invoke mode is not online
Given refreshed credit decision is decline at ASM
And APAPCA is invoked by galaxy offline
And call to sendCommunication for decline gives error
When there is a call to APAPCA
Then update application status as decline

Scenario: 12. APAPCA updates Application status as approved (1002) when asm decision is approved
Given refreshed credit decision is approved at ASM
When there is a call to APAPCA with approved ASM
Then update application status as approved