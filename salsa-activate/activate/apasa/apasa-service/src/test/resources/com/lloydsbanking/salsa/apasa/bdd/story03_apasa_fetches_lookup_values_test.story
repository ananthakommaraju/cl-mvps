Meta:

Narrative:
Activate Product Arrangement service retrieve lookup values from PAM

Scenario: 1. APASA retrieve lookup values from PAM
Given PAM contains lookup values for encryption key and purpose of account
When there is a call to APAD
Then service continues

Scenario: 2. APASA throws error if lookup data is not available
Given PAM does not contain lookup values for encryption key and purpose of account
When there is a call to APAD
Then service throws error



