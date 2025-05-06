Meta:

Narrative:
As the credit card journey I want to retrieve the products offered

Scenario: 1 OPACC returns offered products and ASM status as accept and application type as 10001

Given application status is accept and rpc is called
And product eligibility type of applied product is CO_HOLD
And number of offered products with product Eligibility type as CO_HOLD is greater than 0
When UI calls OPACC with valid request
Then OPACC returns offered products
And application status as accept and application type as 10001

