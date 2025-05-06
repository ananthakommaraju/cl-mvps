Meta:

Narrative:
As a Unauth Sales Journey I want to check sira decision so that complete the activation of valid application


Scenario: 1. APAPCA call sendCommunication to send application information to customer when invoke mode is offline (4) and sira decision is refer
Given refreshed credit decision is refer at SIRA
And APAPCA is invoked by galaxy offline
And call to sendCommunication for referred succeeds
When there is a call to APAPCA
Then service continues

Scenario: 2. APAPCA sendCommunication call returns error and service continues when sira decision is refer
Given refreshed credit decision is refer at SIRA
And APAPCA is invoked by galaxy offline
And call to sendCommunication for referred gives error
When there is a call to APAPCA
Then service continues

Scenario: 3. APAPCA call retrieveLookupValues with REFERRAL_TEAM_GROUPS when sira decision is refer
Given refreshed credit decision is refer at SIRA
And APAPCA is invoked by galaxy offline
And call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds
When there is a call to APAPCA
Then service continues


Scenario: 4. APAPCA call retrieve referral team details with success when sira decision is refer
Given refreshed credit decision is refer at SIRA
And APAPCA is invoked by galaxy offline
And call to retrieveReferralTeamDetails succeeds
When there is a call to APAPCA
Then service continues

Scenario: 5. APAPCA task creation call succeeds and service assign application status and referral code in response when sira decision is refer
Given refreshed credit decision is refer at SIRA
And APAPCA is invoked by galaxy offline
And call to task creation succeeds
When there is a call to APAPCA
Then service set referral details in response

Scenario: 6. APAPCA task creation call fails and service assign application status and extra conditions in response when sira decision is refer
Given refreshed credit decision is refer at SIRA
And APAPCA is invoked by galaxy offline
And call to task creation fails
When there is a call to APAPCA
Then service set extra conditions in response
