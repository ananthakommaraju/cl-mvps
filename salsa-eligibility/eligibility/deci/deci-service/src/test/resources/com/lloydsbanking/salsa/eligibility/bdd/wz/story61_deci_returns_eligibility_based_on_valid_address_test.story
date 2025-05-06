Meta:

Narrative:
Determine eligible customer instruction service should check Address Validation to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true when provided structured address is valid

Given user is applying for product with valid structured address and rule is CR047
When the UI calls DECI for product
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility true when provided unStructured address is valid

Given user is applying for product with valid unStructured address and rule is CR047
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true when provided unStructured is invalid

Given user is applying for product with inValid unStructured address and rule is CR047
When the UI calls DECI for product
Then DECI evaluates eligibility to false and result as Address Validation Failed




