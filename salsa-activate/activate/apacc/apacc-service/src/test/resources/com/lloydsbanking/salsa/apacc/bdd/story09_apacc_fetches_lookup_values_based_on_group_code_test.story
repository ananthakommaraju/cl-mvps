Meta:

Narrative:
Activate Product Arrangement service retrieve lookup values from PAM based on group code

Scenario: 1. APACC returns country code map when group code is evidence or purpose or acquire country data
Given Fulfill credit card arrangement is called
Given there is a internal call to "Retrieve Look Up values" where invoke parameter is group code is valid
When there is a call for APACC
Then verify that database is accessed successfully

Scenario: 2. APACC throws data not available error if group code is not evidence or purpose or acquire country data
Given Fulfill credit card arrangement is called
Given there is a internal call to "Retrieve Look Up values" where invoke parameter is group code is not valid
When there is a call for APACC
Then service continues