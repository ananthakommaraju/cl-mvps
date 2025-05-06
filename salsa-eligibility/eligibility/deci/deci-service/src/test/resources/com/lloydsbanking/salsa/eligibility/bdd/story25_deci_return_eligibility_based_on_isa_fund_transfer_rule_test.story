Meta:

Narrative:
Determine eligible customer instruction service should check max amount and head room amounts
to determine eligibility for ISA products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
 ISA when max amount and head room amounts are equal

Given max amount and head room amount are set equal on a ISA account
And rule is CR015
When the UI calls DECI for ISA
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
 ISA when max amount and head room amounts are not equal

Given max amount and head room amount are not equal on a ISA account
And rule is CR015
When the UI calls DECI for ISA
Then DECI evaluates eligibility to false

