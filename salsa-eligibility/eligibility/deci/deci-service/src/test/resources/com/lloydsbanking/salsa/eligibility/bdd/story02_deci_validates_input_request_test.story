Meta:

Narrative:
As a user interface

I want determine eligible customer instruction service to do a validate check on request
So that I can display appropriate warning message in case requested in not valid

Scenario: 1. DECI service responds when customer arrangement in request

Given DECI is running
When the UI calls DECI with valid customer arrangement
Then DECI returns customerInstructions in response

Scenario: 2. DECI service throws error when no customer arrangement in request

Given DECI is running
When the UI calls DECI with no customer arrangement
Then DECI returns error for missing customer arrangement

Scenario: 3. DECI service responds when valid Account Type value is in customer arrangements

Given DECI is running
When the UI calls DECI with valid Account Type in customer arrangements
Then DECI returns customerInstructions in response

Scenario: 4. DECI service returns error when no valid Account Type value in customer arrangements

Given DECI is running
When the UI calls DECI with invalid Account Type in customer arrangements
Then DECI returns error for invalid Account Type in response