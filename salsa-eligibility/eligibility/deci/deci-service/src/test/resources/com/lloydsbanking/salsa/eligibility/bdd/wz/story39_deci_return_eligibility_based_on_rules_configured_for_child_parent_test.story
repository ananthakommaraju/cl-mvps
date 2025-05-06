Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for candidate instruction mnemonic based on rules configured for parent instruction mnemonic
if rules doesn't exists for child instruction mnemonic

Scenario: 1. DECI service evaluates the rule of candidate instructions for which rule is configured
Given instruction rules are mapped for child Instruction
When UI calls DECI with a valid request
Then DECI evaluates eligibility based on candidate instruction mnemonic

Scenario: 2. DECI service evaluates the rule of candidate instructions for which rule is not configured
Given instruction rules are mapped for parent Instruction
When UI calls DECI with a valid request
Then DECI evaluates eligibility based on parent instruction mnemonic of candidate instruction





