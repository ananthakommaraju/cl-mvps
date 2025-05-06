Narrative:
As a user interface
I want determine eligible customer instruction service fetches parent instruction
So that I can check the eligibility

Scenario: 1. DECI service responds when parent instructions are available
Given Parent Instructions are available for Instructions
When the UI calls DECI with valid request
Then DECI returns customerInstructions in response

Scenario: 2. DECI service throws error for unavailable parent instructions
Given Parent Instructions are unavailable for Instructions
When the UI calls DECI with valid request
Then DECI returns errorcode for unavailable parent instruction



