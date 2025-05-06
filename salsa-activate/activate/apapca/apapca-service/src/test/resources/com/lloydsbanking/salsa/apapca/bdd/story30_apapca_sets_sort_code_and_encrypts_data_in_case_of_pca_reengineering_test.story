Meta:

Narrative:
As a Unauth Sales Journey I want to set sort code and encrypt data, so that I can complete the activation of a valid application

Scenario: 1. APAPCA encrypts data when INTEND_TO_SWITCH is present in rule condition
Given that sort code is present in request
And PAM contains lookup values for encryption key and purpose of account
When there is a call to APAD
Then service responds

Scenario: 2. APAPCA calls H071 and encrypts data when INTEND_TO_SWITCH is present in rule condition and sort code is not present
Given that sort code is not present in request
And H071 is called with success
And PAM contains lookup values for encryption key and purpose of account
When there is a call to APAD
Then service responds
