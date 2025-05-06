Meta:

Narrative:
Determine eligible customer instruction service should check customer is having offshore accounts

Scenario: 1.Determine eligible customer instruction service evaluates eligibility false when sort code
and threshold are same

Given there is rule CR013 configured CR013 with threshold 301642
When deci is called with Product Arrangement having sort code 301642
Then deci returns eligibility false
And Reason description for rule CR013

Scenario: 2.Determine eligible customer instruction service evaluates eligibility true when sort code
and threshold are different

Given there is rule CR013 configured CR013 with threshold 301996
When deci is called with Product Arrangement having sort code 301642
Then deci returns eligibility true

