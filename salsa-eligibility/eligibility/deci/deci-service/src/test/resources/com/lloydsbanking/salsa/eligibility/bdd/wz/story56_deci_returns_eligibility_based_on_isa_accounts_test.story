Meta:

Narrative:
Determine eligible customer instruction service should check Maximum number of fixed rate ISA’s held to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true when existing arrangements doesnot exist

Given existing arrangement does not exits in the request
And rule is CR035
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true when fixed rate ISA not held in the customers existing product

Given fixed rate isa notheld in customers existing product
And rule is CR035
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false when  fixed rate ISA held in the customers existing product

Given fixed rate isa held in customers existing product
And rule is CR035
When the UI calls DECI for product
Then DECI evaluates eligibility to false and returns Maximum number of fixed rate ISA’s held




