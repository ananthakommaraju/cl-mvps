Meta:

Narrative:
I want to call CBS API E226 to add overdraft details to customer account so that I can
complete activation of a valid application

Scenario: 1. APAPCA calls E226 when Application sub status is null or 1024 and B765 call passes
Given Fulfill bank account arrangement is called with null or 1024 substatus request
And B765 call passes
And E226 adds overdraft detail
When APAD is called with Application sub status is 1024 or null
Then APAPCA returns response

