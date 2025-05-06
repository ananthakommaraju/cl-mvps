Meta:

Narrative:
As a Unauth Sales Journey I want to encrypt credit card numbers, so that I can complete the activation of a valid application

Scenario: 1. APACC encrypts data when credit card number does not exceeds its maximum length
Given that credit card number is not null
And Encrypt data service responds successfully
When there is a call to APAD
Then service responds

Scenario: 2. APACC sets credit card number as null if encrypt data service responds with error
Given that credit card number is not null
And Encrypt data service responds with error
When there is a call to APAD
Then service responds
