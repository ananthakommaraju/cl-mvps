Meta:

Narrative:
As the loans journey I want OPALOANS to determine the eligible loan products of customer and returns the customer score and customer number.

Scenario: 1 OPALOANS determines the eligible loan products of customer and returns the customer score and customer number when customer identifier is available

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with error code 0
When UI calls OPALOANS with valid request
Then OPALOANS responds with customer number and customer score with score result as ACCEPT
And OPALOANS returns the eligible loan products of customer in response

Scenario: 2 OPALOANS fails to determine the eligible loan products of customer

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with error code 823005
When UI calls OPALOANS with valid request
Then OPALOANS responds with external business error