Meta:

Narrative:
AdministerProductSelection responds to valid request with proper response

Scenario: 1 APS returns CO_HOLD for customer having a Advance card and applying for Platinum master card
Given Customer holds a Advance Credit Card
And Customer is eligible to co hold Advance card with Platinum Balance Transfer Card
When UI calls APS with valid request
Then APS returns Application Type Code as CO_HOLD in response

Scenario: 2 APS returns INELIGIBLE for customer having a Advance card and applying for another Advance Card due to max eligible card check
Given Customer holds a Advance Credit Card
And max eligible value is one for Advance Credit Card
When UI calls APS with valid request
Then APS returns Application Type Code as INELIGIBLE in response

Scenario: 3 APS returns CO_HOLD for customer having a Advance card and applying for another Advance Card
Given Customer holds a Advance Credit Card
And max eligible value is two for Advance Credit Card
When UI calls APS with valid request
Then APS returns Application Type Code as CO_HOLD in response

Scenario: 4 APS returns INELIGIBLE for customer having a Advance card and and applying for Platinum master card due to Invalid eligibility
Given Customer holds a Advance Credit Card
And Customer is not eligible to co hold Advance card with Platinum Balance Transfer Card
When UI calls APS with valid request
Then APS returns Application Type Code as INELIGIBLE in response

Scenario: 5 APS returns Internal Service Error if Product Type does not match
Given Product Type does not match for Applied and Existing product
When UI calls APS with valid request
Then APS throws Internal Service error


