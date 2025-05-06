Meta:

Narrative:
Determine eligible customer instruction service should check FEPS loan current account and channel credit card
to determine eligibility for personal loan

Scenario: 1. Determine eligible customer instruction service evaluates eligibility false for personal loan which have FEPSLoan Current account and Channel loan account is notavailable and rule is CR024

Given FEPS loan is notAvailable and Current account is notAvailable and Channel loan account is notAvailable for the customer
And rule is CR024
When the UI calls DECI for product
Then DECI evaluates eligibility to false

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for personal loan which have FEPSLoan or  Channel loan account available and Current account is notAvailable and rule is CR025
Given FEPS loan is notAvailable and Current account is notAvailable and Channel loan account is available for the customer
And rule is CR025
When the UI calls DECI for product
Then DECI evaluates eligibility to false

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false for personal loan which have current account not available

Given current account notAvailable for the customer
And rule is CR027
When the UI calls DECI for product
Then DECI evaluates eligibility to false

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for Credit Card which have more than 1 credit Card products

Given customer has more than 1 credit cards
And rule is CR031
When the UI calls DECI for product
Then DECI evaluates eligibility to false

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for Credit card which has less than 2 credit Card products

Given customer has less than 2 credit cards
And rule is CR031
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 6. Determine eligible customer instruction service evaluates eligibility true for personal loan which have current account available and rule is CR027

Given current account available for the customer
And rule is CR027
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 7. Determine eligible customer instruction service evaluates eligibility true for personal loan which have FEPSLoan or  Channel loan account notAvailable and Current account is notAvailable and rule is CR025
Given FEPS loan is available and Current account is available and Channel loan account is notAvailable for the customer
And rule is CR025
When the UI calls DECI for product
Then DECI evaluates eligibility to true


Scenario: 8. Determine eligible customer instruction service evaluates eligibility true for personal loan which have FEPSLoan Current account and Channel loan account is available and rule is CR024

Given FEPS loan is available and Current account is available and Channel loan account is available for the customer
And rule is CR024
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 9. Determine eligible customer instruction service evaluates eligibility true for personal loan which have FEPSLoan available and Current account not available and Channel loan account is available and rule is CR024

Given FEPS loan is available and Current account is notAvailable and Channel loan account is available for the customer
And rule is CR024
When the UI calls DECI for product
Then DECI evaluates eligibility to true