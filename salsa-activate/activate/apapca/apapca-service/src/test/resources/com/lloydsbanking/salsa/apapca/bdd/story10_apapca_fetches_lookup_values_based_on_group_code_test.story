Meta:

Narrative:
Activate Product Arrangement service retrieve lookup values from PAM based on group code i.e. ISO_COUNTRY_CODE

Scenario: 1. APAPCA returns country code map when group code is ISO_COUNTRY_CODE
Given Fulfill bank account arrangement is called
Given there is a internal call to "Retrieve Look Up values" where invoke parameter is group code and channel
When there is a call for APAD
Then verify that database is accessed successfully

Scenario: 2. APAPCA throws data not available error if group code is not ISO_COUNTRY_CODE
!-- Given Fulfill bank account arrangement is called
!-- Given there is a internal call to "Retrieve Look Up values" where invoke parameter is group code is not ISO_COUNTRY_CODE
!-- When there is a call for APAD
!-- Then APAD throws data not available error