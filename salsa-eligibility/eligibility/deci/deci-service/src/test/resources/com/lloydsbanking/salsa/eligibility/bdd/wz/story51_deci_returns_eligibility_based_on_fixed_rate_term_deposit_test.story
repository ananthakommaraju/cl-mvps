Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for fixed rate term deposit("G_ONL_FRTD", "G_FR_TD") based
on instruction mnemonic and parent instruction mnemonic to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers
with not more than 5 product holdings of fixed rate term deposit (G_ONL_FRTD)

Given customer doesn't have more than 5 product holdings of fixed rate term deposit (G_ONL_FRTD)
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customers
with more than 5 product holdings of fixed rate term deposit

Given customer have more than 5 product holdings of fixed rate term deposit (G_ONL_FRTD)
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns description for CR028

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customers
with not more than 5 product holdings of fixed rate term deposit (G_FR_TD)

Given customer doesn't have more than 5 product holdings of fixed rate term deposit (G_FR_TD)
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for customers
with more than 5 product holdings of fixed rate term deposit (G_FR_TD)

Given customer have more than 5 product holdings of fixed rate term deposit (G_FR_TD)
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns description for CR028

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for customer
not having any existing product arrangement

Given customer does not have any existing product arrangement and rule is CR028
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true