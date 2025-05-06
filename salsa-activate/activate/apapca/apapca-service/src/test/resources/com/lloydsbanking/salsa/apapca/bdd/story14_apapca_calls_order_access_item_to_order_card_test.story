Narrative:
I want to call Order Access service Order Access Item to call CMAS APIs for ordering cards so that
I can complete activation of a valid application

Scenario: 1. APAPCA calls OrderAccess Service when Application sub status is 1024 and Debit Card Required flag is true
Given Fulfill bank account arrangement is called and debit card required is true
And B765 call passes
And E226 call passes
And Order Access service call passes
When there is a call to APAD
Then APAPCA returns response

Scenario: 2. APAPCA calls OrderAccess Service fails and updates status as Awaiting fulfillment(1009) and sub status as Failed to create card order (1026)
and increases retry count by 1
!-- Given Fulfill bank account arrangement is called and debit card required is true
!-- And B765 call passes
!-- And E226 call passes
!-- And Order Access service call fails
!-- When there is a call to APAD
!-- Then APAPCA returns response with status as Awaiting fulfillment(1009)
