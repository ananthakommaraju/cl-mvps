Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for candidate instruction mnemonic based on rules configured for parent instruction mnemonic
if rules doesn't exists for child instruction mnemonic

Scenario: 1. DECI service evaluates the rule of candidate instructions for which rule is not configured
When the UI calls DECI with a candidate instruction that has no rules
Then deci evaluates eligibility based on parent instruction mnemonic of candidate instruction


Scenario: 2. DECI service evaluates the rule of candidate instructions for which rule is configured
When the UI calls DECI with a candidate instruction that has rules
Then deci evaluates eligibility based on candidate instruction mnemonic


