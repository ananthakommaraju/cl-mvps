Meta:

Narrative:
As a Unauth Sales Journey I want to check asm decision so that complete the activation of valid application

Scenario: 1. APASA calls F425 to retrieve updated ASM decision with credit score source system as 025 when application status is referred (1003)
Given there is call to AdministerReferredArrangement for referred application
When there is a call to APASA
Then call ASM API F425 to retrieve updated ASM decision with credit score source system as 025

Scenario: 2. APASA updates application status as Awaiting Referral Processing (1008) when F425 gives error
Given call to ASM F425 gives error
When there is a call to APASA
Then update application status as awaiting referral Processing

Scenario: 3. APASA set Application status as no modification required and pam will not be updated when invoke mode is online (1) and asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy online
When there is a call to APASA
Then update application status as no modification required

Scenario: 4. APASA call sendCommunication to send application information to customer when invoke mode is offline (4) and asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to sendCommunication for referred succeeds
When there is a call to APASA
Then service continues

Scenario: 5. APASA sendCommunication call returns error and service continues when asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to sendCommunication for referred gives error
When there is a call to APASA
Then service continues

Scenario: 6. APASA call retrieveLookupValues with REFERRAL_TEAM_GROUPS when asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds
When there is a call to APASA
Then service continues

Scenario: 7. APASA call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001 when retrieveLookupValues with REFERRAL_TEAM_GROUPS fails
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS fails
When there is a call to APASA
Then call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001

Scenario: 8. APASA call retrieve referral team details with success when asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to retrieveReferralTeamDetails succeeds
When there is a call to APASA
Then service continues

Scenario: 9. APASA task creation call succeeds and service assign application status and referral code in response when asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to task creation succeeds
When there is a call to APASA
Then service set referral details in response

Scenario: 10. APASA task creation call fails and service assign application status and extra conditions in response when asm decision is refer
Given refreshed credit decision is refer at ASM
And APASA is invoked by galaxy offline
And call to task creation fails
When there is a call to APASA
Then service set extra conditions in response

Scenario: 11. APASA updates application status as decline (1004) when asm decision is decline and invoke mode is online (1)
Given refreshed credit decision is decline at ASM
And APASA is invoked by galaxy online
When there is a call to APASA
Then update application status as decline

Scenario: 12. APASA updates Application status as decline (1004) when call send to communication succeeds and asm decision is decline and invoke mode is not online
Given refreshed credit decision is decline at ASM
And APASA is invoked by galaxy offline
And call to sendCommunication for decline succeeds
When there is a call to APASA
Then update application status as decline

Scenario: 13. APASA updates Application status as decline (1004) when send communication fails and asm decision is decline and invoke mode is not online
Given refreshed credit decision is decline at ASM
And APASA is invoked by galaxy offline
And call to sendCommunication for decline gives error
When there is a call to APASA
Then update application status as decline

Scenario: 14. APASA updates Application status as approved (1002) when asm decision is approved
Given refreshed credit decision is approved at ASM
When there is a call to APASA with call of processPostFulfil
Then set credit limit and application status as approved