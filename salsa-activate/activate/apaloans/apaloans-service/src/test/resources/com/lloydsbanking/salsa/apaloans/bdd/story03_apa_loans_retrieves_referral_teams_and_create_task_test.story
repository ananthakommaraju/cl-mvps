Meta:

Narrative:
As a Unauth Sales Journey I want to retrieve refeerral teams and create tms task to complete the activation of valid application

Scenario: 1. APALOANS call retrieve referral team details with success
Given call to retrieveReferralTeamDetails succeeds
When there is a call to APALOANS
Then service continues

Scenario: 2. APALOANS returns error when retrieve referral team details got failed
!-- Given call to retrieveReferralTeamDetails fails
!-- When there is a call to APALOANS
!-- Then service returns data not available error

Scenario: 3. APALOANS set application status and referral details when task creation call succeeds
Given tms task creation succeeds
When there is a call to APALOANS
Then service set application status as Awaiting Referral Processing

Scenario: 4. APALOANS return error tms task creation call fails
!-- Given tms task creation fails
!-- When there is a call to APALOANS
!-- Then service returns internal service error