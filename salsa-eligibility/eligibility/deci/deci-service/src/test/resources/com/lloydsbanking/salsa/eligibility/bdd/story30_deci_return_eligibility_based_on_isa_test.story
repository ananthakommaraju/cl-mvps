Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for ISA opened this tax year


Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for ISA if ISA not opened this tax year
Given  ISA is not opened this Tax year
When UI calls DECI for ISA
Then DECI evaluates eligibility as true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for ISA if ISA opened this tax year
Given  ISA is opened this Tax year
When UI calls DECI for ISA
Then DECI evaluates eligibility to false  and returns  error condition for already existing ISA in the given tax year
