Narrative:
As a user interface
I want determine eligible customer instruction service fetches child instruction
So that I can check the eligibility

Scenario: 1. DECI service responds when child instruction is available
When the UI calls DECI with a candidate instruction that has no children
Then DECI returns CustomerInstructions of Child Instructions in response

Scenario: 2. DECI service returns CustomerInstructions of Candidate Instruction in response
When the UI calls DECI with a candidate instruction that has children
Then DECI returns CustomerInstructions of Candidate Instruction in response