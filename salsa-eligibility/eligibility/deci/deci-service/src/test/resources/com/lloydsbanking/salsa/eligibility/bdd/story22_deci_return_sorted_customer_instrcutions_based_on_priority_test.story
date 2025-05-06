Meta:

Narrative:
Determine eligible customer instruction service returns eligible customer instructions sorted on basis of priority

Scenario: 1. Determine eligible customer service returns eligible customer instructions sorted on basis of priority

Given customer is applying for a product and have eligible products
When the UI calls DECI with valid request
Then DECI returns customer instructions sorted on basis of priority

