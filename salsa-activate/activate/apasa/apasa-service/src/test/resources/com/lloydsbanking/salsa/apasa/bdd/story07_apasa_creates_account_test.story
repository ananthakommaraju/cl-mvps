Meta:

Narrative:
Activate Product Arrangement service calls B675 to create account at CBS end

Scenario: 1. APASA creates account at CBS end with help of B675 when Application sub status is null or 1024
Given Fulfill pending saving account  is called
And B765 creates account at CBS end
When APAD is called with Application sub status is null
Then APASA returns account number of account created

Scenario: 2. APASA updates  application status as 1009 (Awaiting Fulfillment) when B675 call fails
Given Fulfill pending saving account  is called
And B675 call fails
When APAD is called with Application sub status is null
Then APASA returns App status as Awaiting fulfilment 1009
