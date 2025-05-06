Meta:

Narrative:
As the credit card journey I want to check eidv status of the customer

Scenario: 1 OPAPCA returns customer score in response with EIDV status Decline

Given Customer is not identified at Experian
When UI calls OPAPCA with valid request
Then OPAPCA returns Customer Score as EIDV status Decline

Scenario: 2 OPAPCA returns customer score in response with EIDV status Refer

Given Customer is referred at Experian
When UI calls OPAPCA with valid request
Then OPAPCA returns Customer Score as EIDV status Refer

Scenario: 3 OPAPCA returns customer score in response with EIDV status Approve

Given Customer is approved at Experian
When UI calls OPAPCA with valid request
Then OPAPCA returns Customer Score as EIDV status Approved



