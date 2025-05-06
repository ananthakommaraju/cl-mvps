Meta:

Narrative:
Determine eligible customer instruction service should check current account of logged in channel and loan to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility false when Loan is notAvailable and Current account is notAvailable in the customers existing product

Given Loan is notAvailable and Current account is notAvailable for the customers existing product
And rule is CR025
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false when Loan is notAvailable and Current account is available in the customers existing product

Given Loan is notAvailable and Current account is Available for the customers existing product
And rule is CR025
When the UI calls DECI for product
Then DECI evaluates eligibility to true


