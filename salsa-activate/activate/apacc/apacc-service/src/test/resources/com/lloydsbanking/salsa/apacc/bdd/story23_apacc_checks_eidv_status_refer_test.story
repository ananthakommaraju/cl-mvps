Meta:

Narrative:
As a Unauth Sales Journey I want to check eidv status and set fulfil eligibility flag so that complete the activation of valid application


Scenario: 1. APACC sends communication to customer and set application status as manual idv (1007) when the application status is approved (1002) and related application id is present and eidv status is refer
Given updated Application status is approved
And related application id is not present in request
And eidv status is refer
When there is call to APACC
Then call send communication and set application status as manual idv

Scenario: 2. APAPCC updates application status as Awaiting Referral Processing (1008) when F425 gives error
Given call to ASM F425 gives error
When there is a call to APAPCC
Then update application status as awaiting referral Processing


Scenario: 3. APAPCC call sendCommunication to send application information to customer when invoke mode is offline (4) and asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to sendCommunication for referred succeeds
When there is a call to APAPCC
Then service continues

Scenario: 4. APAPCC sendCommunication call returns error and service continues when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to sendCommunication for referred gives error
When there is a call to APAPCC
Then service continues

Scenario: 5. APAPCC call retrieveLookupValues with REFERRAL_TEAM_GROUPS when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds
When there is a call to APAPCC
Then service continues

Scenario: 6. APAPCC call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001 when retrieveLookupValues with REFERRAL_TEAM_GROUPS fails
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS fails
When there is a call to APAPCC
Then call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001

Scenario: 7. APAPCC call retrieve referral team details with success when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to retrieveReferralTeamDetails succeeds
When there is a call to APAPCC
Then service continues


Scenario: 8. APAPCC task creation call succeeds and service assign application status and referral code in response when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to task creation succeeds
When there is a call to APAPCC
Then service set referral details in response

Scenario: 9. APAPCC task creation call fails and service assign application status and extra conditions in response when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to task creation fails
When there is a call to APAPCC
Then service set extra conditions in response

Scenario: 10. APAPCC updates application status as decline (1004) when asm decision is decline and invoke mode is online (1)
Given refreshed credit decision is decline at ASM
And APAPCC is invoked by galaxy online
When there is a call to APAPCC
Then update application status as decline

Scenario: 11. APAPCC updates Application status as decline (1004) when call send to communication succeeds and asm decision is decline and invoke mode is not online
Given refreshed credit decision is decline at ASM
And APAPCC is invoked by galaxy offline
And call to sendCommunication for decline succeeds
When there is a call to APAPCC
Then update application status as decline

Scenario: 12. APAPCC updates Application status as decline (1004) when send communication fails and asm decision is decline and invoke mode is not online
Given refreshed credit decision is decline at ASM
And APAPCC is invoked by galaxy offline
And call to sendCommunication for decline gives error
When there is a call to APAPCC
Then update application status as decline








