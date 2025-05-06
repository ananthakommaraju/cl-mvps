Meta:

Narrative:
As the saving account journey I want to check eidv status of the customer

Scenario: 2 OpaSaving returns customer score in response with EIDV status Refer

Given Customer is referred at Experian
When UI calls OpaSaving with valid request
Then OpaSaving returns Customer Score as EIDV status Refer

Scenario: 3 OpaSaving returns customer score in response with EIDV status Approve

Given Customer is approved at Experian
When UI calls OpaSaving with valid request
Then OpaSaving returns Customer Score as EIDV status Approved

Scenario: 4 OpaSaving returns customer score in response with EIDV status Refer for BFPO Address
Given Customer contains BFPO address indicator
When UI calls OpaSaving with valid request
Then OpaSaving returns Customer Score as EIDV status Refer

