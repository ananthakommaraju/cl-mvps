Meta:

Narrative:
Determine eligible customer instruction service should check if ISA has been funded this year
to determine eligibility for ISA products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true when isa product is available and rule is CR015

Given user is applying for isa product with no arrangement and rule is CR015
When the UI calls DECI for product
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for isa product is available and rule is CR015

Given user is applying for isa product and the existing isa has been not funded and rule is CR015
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false for isa product is available and rule is CR015

Given user is applying for isa product and the existing isa has been funded and rule is CR015
When the UI calls DECI for product
Then DECI evaluates eligibility to false and return Funds have been deposited this year
