Meta:

Narrative:
Activate Product Arrangement service retrieve lookup values from PAM

Scenario: 1. APAPCA retrieve arrangement details from pam if arrangement id is present in request
Given request contains arrangement Id
When there is a call to APAD
Then service sets arrangement details in product arrangement

Scenario: 2. APAPCA retrieve arrangement details from pam for related application id
!-- Given request contains arrangement id with related application available
!-- When there is a call to APAD
!-- Then service sets arrangement details in product arrangement





