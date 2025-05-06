Meta:

Narrative:
Activate Product Arrangement send communication email for IB registration success based on access level

Scenario: 1. APACC send communication for ib registration with template IB_STP_LITE_REGISTRATION_SUCCESS_MAIL for lite user access level
Given IB application activation is successful
And user access level is lite
When there is a call to APACC
Then send communication for successful registration with template IB_STP_LITE_REGISTRATION_SUCCESS_MAIL

Scenario: 2. APACC send communication for ib registration with template IB_STP_REGISTRATION_SUCCESS_MAIL for ultra lite user access level
Given IB application activation is successful
And user access level is ultra lite
When there is a call to APACC
Then send communication for successful registration with template IB_STP_REGISTRATION_SUCCESS_MAIL


