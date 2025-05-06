Meta:

Narrative:
Activate Product Arrangement service calls B675 to create account at CBS end

Scenario: 1. APAPCA creates account at CBS end with help of B675 when Application sub status is null or 1024
Given Fulfill bank account arrangement is called
And B765 creates account at CBS end
When APAD is called with Application sub status is null or 1024
Then APAPCA returns account number of account created

Scenario: 2. APAPCA updates  application status and substatus when B675 call fails
!-- Given Fulfill bank account arrangement is called
!-- And B675 call fails
!-- When APAD is called with Application when B675 fails
!-- Then APAPCA returns App status as Awaiting fulfilment 1009, Sub status as Current Account creation failure 1024 and increment Retry counter by 1
