Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for candidate instruction mnemonic
or child mnemonics if child mnemonic exists

Scenario: 1. Return eligibility status for all child mnemonics

Given Candidate instruction has 3 child instruction mnemonics
When deci is called
Then deci returns eligibility for all 3 child mnemonics

Scenario: 2. Return eligibility status for candidate instruction mnemonic when no child mnemonic exists

Given Candidate instruction is a child instruction
When deci is called
Then deci returns eligibility for candidate instruction mnemonic



