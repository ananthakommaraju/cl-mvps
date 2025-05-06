Meta:

Narrative:
As a Unauth Sales Journey I want to allocate order access item, so that I can complete the activation of a valid application

Scenario: 1. APAPCA fetches order access item when order is declined
Given Application Status and Source system Identifier is valid for DB Event call
And C808 is called to initiate card order
And C846 is called to retrieve eligible cards
And C812 returns the order status as declined
When there is a call to APAD
Then service responds


Scenario: 2. APAPCA fetches order access item when order is not declined
!-- Given Application Status and Source system Identifier is valid for DB Event call
!-- And C808 is called to initiate card order
!-- And C846 is called to retrieve eligible cards
!-- And C812 returns the order status as approved
!-- And C818 is called to validate card order
!-- When there is a call to APAD
!-- Then service responds

