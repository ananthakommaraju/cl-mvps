Meta:

Narrative:
Activate Product Arrangement service validates validity of Application based on App Status and Source system Identifier

Scenario: 1. APACC continues for a Valid DB event Request
! --Given Application Status and Source system Identifier is valid for DB Event call
! --When there is a call to APACC
! --Then service continues

Scenario: 2. APACC throws error if request is not valid
Given Application status and Source system Identifier is invalid
When there is a call to APACC
Then service throws error

Scenario: 3. APACC continues for a Valid Galaxy Online Request
Given Application Status and Source system Identifier is valid for Galaxy Online call
When there is a call to APACC
Then service continues