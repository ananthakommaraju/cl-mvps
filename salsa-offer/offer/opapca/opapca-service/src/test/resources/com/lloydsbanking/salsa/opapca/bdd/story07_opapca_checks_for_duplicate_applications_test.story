Meta:

Narrative:
OfferProductArrangementPCA responds checks for Duplicate application

Scenario: 1 OPAPCA responds to with duplicate application error
Given Duplicate applications exists in PAM
And applications status is not ASM decline
When UI calls OPAPCA with valid request
Then OPAPCA returns duplicate application error in response

Scenario: 2 OPAPCA responds to with proper response when no duplicate application exists
Given No duplicate applications exists in PAM
When UI calls OPAPCA with valid request
Then OPAPCA returns valid response
