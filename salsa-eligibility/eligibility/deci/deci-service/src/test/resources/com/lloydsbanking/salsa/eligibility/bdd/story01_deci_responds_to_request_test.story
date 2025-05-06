Meta:

Narrative:
As a user interface
I want determine eligible customer instruction service responds to requests
So that I can check the eligibility

Scenario: 1. Determine eligible customer instruction service responds to requests

Given DECI is running
When the UI calls DECI with valid request
Then DECI responds

Scenario: 2. DECI returns error to invalid requests

Given DECI is running
When the UI calls DECI with invalid requests
Then DECI responds with error

Scenario: 3. DECI returns error to invalid requests

Given invalid rule type is returned for customer instruction
When the UI calls DECI
Then DECI responds with internal service error