Meta:

Narrative:
Service will call createIBApplication if ApplicationType is new or Joint and Ib registration required

Scenario: 1. APAPCA calls B750 when Application is not eligible for fulfilment and eligible for IB registration
Given Application is valid for IB Registration
And sort code is present in request
And activate IB Registration is called when B750 is called
When there is a call to APAD
Then service continues


Scenario: 2. APAPCA continues if Application is eligible for fulfilment
!-- Given Application type is eligible for fulfilment
!-- And sort code is present in request
!-- And activate IB Registration is called when B750 is not called
!-- When there is a call to APAD
!-- Then service continues


Scenario:3. APAPCA throws internal service error if application type is not valid
!-- Given Application Type is invalid
!-- When there is  a call to APAD For Error
!-- Then Throw Invalid Application type Error







