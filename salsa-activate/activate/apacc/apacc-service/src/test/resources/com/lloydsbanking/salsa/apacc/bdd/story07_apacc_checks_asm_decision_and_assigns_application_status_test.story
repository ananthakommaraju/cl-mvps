Meta:

Narrative:
As a Unauth Sales Journey I want to check asm decision so that complete the activation of valid application

Scenario: 1. APAPCC calls F425 to retrieve updated ASM decision with credit score source system as 024 when application status is referred (1003)
Given there is call to AdministerReferredArrangement for referred application
When there is a call to APAPCC
Then call ASM API F425 to retrieve updated ASM decision with credit score source system as 024

Scenario: 2. APAPCC set Application status as no modification required and pam will not be updated when invoke mode is online (1) and asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy online
When there is a call to APAPCC
Then update application status as no modification required

Scenario: 3. APAPCC returns error when retrieve referral team details got failed when asm decision is refer
Given refreshed credit decision is refer at ASM
And APAPCC is invoked by galaxy offline
And call to retrieveReferralTeamDetails fails
When there is a call to APAPCC
Then service returns error

Scenario: 4. APAPCC updates Application status as approved (1002) when asm decision is approved
Given refreshed credit decision is approved at ASM
When there is a call to APAPCC
Then set credit limit and application status as approved