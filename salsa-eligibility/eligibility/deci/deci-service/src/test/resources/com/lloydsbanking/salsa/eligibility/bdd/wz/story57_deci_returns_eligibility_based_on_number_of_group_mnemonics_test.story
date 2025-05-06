Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based
on threshold mnemonic and threshold count to determine eligibility.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers
having products less than maximum number of products defined for a mnemonics group

Given customer has products that does not exceed the maximum products defined for a mnemonic group and rule is CR043
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customers
having products greater than maximum number of products defined for a mnemonics group

Given customer has products that does exceed the maximum products defined for a mnemonic group and rule is CR043
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer cannot have more than threshold products for group of mnemonics defined (cr 043 rule)


Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customer
not having any existing product arrangement

Given customer does not have any existing product and rule is CR043
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true