Meta:

Narrative:
As a Unauth Sales Journey I want to allocate account number, so that I can complete the activation of a valid application

Scenario: 1. APAPCA fetches cbsappgroup when sort code is present
Given that sort code is present in request
And PAM contains lookup values for encryption key and purpose of account
When there is a call to APAD
Then service responds

Scenario: 2. APAPCA fetches cbsappgroup when sort code is not present
!-- Given that sort code is not present in request
!-- And PAM contains lookup values for encryption key and purpose of account
!-- When there is a call to APAD
!-- Then service responds

