Meta:

Narrative:
Determine eligible customer instruction service should check loan applied times
to determine eligibility for RBBS overdraft

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
RBBS overdraft when loan applied times count is less than set threshold

Given loan applied times is less than threshold for an account for which overdraft is being applied
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
RBBS overdraft when loan applied times count is more than set threshold

Given loan applied times is more than threshold for an account for which overdraft is being applied
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to false for RBBS overdraft and returns error condition