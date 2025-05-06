Meta:

Narrative:
OfferProductArrangementCC responds checks for Duplicate application

Scenario: 1 OPACC responds to with duplicate application error
Given Duplicate applications exists in PAM
And applications status is not ASM decline
When UI calls OPACC with valid request
Then OPACC returns duplicate application error in response

Scenario: 2 OPACC responds to with duplicate application error with ASM decline

Given Duplicate applications exists in PAM
And applications status is ASM decline
When UI calls OPACC with valid request
Then OPACC returns duplicate application with ASM decline error in response


Scenario: 3 OPACC responds to with proper response when no duplicate application exists
Given No duplicate applications exists in PAM
When UI calls OPACC with valid request
Then OPACC returns valid response
