Meta:

Narrative:
As the credit card journey I want to check eidv status of the customer


Scenario: 2 OPACC returns customer score in response with EIDV status Refer

Given Customer is referred at Experian
When UI calls OPACC with valid request
Then OPACC returns Customer Score as EIDV status Refer


