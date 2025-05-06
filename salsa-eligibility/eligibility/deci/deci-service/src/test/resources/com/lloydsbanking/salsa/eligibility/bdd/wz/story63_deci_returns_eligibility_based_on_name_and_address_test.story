Meta:

Narrative:
Determine eligible customer instruction service should validate Address and Individual name to determine eligibility for requested products


Scenario: 1. Determine eligible customer instruction service evaluates eligibility true when provided unStructured address is valid and individual name is non empty

Given user is applying for product with valid unStructured address and individual name is nonempty
And rule is CR065
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true when provided unStructured address is valid and individual name is empty

Given user is applying for product with valid unStructured address and individual name is empty
And rule is CR065
When the UI calls DECI for product
Then DECI evaluates eligibility to false and result as Name or Address Validation Failed

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true when provided unStructured address is invalid and individual name is non empty

Given user is applying for product with invalid unStructured address and individual name is nonempty
And rule is CR065
When the UI calls DECI for product
Then DECI evaluates eligibility to false and result as Name or Address Validation Failed




