Meta:
Narrative:
As a user interface
I want determine eligible customer instruction service fetches composite instruction
So that I can check the eligibility

Scenario: 1.Determine eligible customer instruction service fetches composite instruction

Given Instruction rules are available for candidate Instructions
When the UI calls DECI with valid request
Then DECI returns CustomerInstructions of candidate Instructions in response

Scenario: 2.DECI fetches rules for parent instructions and returns CustomerInstructions of candidate Instructions

Given Instructions rules are unavailable for candidate Instruction
When the UI calls DECI with valid request
Then DECI fetches rules for parent instructions and returns CustomerInstructions of candidate Instructions