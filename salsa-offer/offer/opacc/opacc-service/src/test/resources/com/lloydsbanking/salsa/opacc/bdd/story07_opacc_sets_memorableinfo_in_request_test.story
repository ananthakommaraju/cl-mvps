Meta:

Narrative:
As the credit card journey I want to encrypt credit card number

Scenario: 2 OPACC returns memorable info as null

Given Memorable info is not present in request
When UI calls OPACC with valid request
Then OPACC returns memorable info as null



