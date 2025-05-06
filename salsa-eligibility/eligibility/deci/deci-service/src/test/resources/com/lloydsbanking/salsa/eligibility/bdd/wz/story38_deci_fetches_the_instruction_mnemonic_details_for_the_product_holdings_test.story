Narrative:
As a user interface
I want determine eligible customer instruction service check if Product Arrangement is present


Scenario: 1. DECI service fetches the instruction mnemonic details from PRD instruction look up view for the product holdings.

Given request  has existing product arrangement present
And instruction mnemonic is present in prd for the existing product arrangement
When the UI calls DECI with a valid request
Then DECI responds


Scenario: 2.   DECI service fetches the instruction mnemonic details from PRD instruction look up view for the product holdings and if no maching instruction found it continue for the next step

Given request  has existing product arrangement present
And instruction mnemonic is not present in prd for the existing product arrangement
When the UI calls DECI with a valid request
Then DECI responds
