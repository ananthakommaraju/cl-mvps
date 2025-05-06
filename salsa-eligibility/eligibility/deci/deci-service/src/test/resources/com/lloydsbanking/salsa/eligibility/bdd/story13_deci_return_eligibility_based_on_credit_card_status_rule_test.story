Meta:

Narrative:
Determine eligible customer instruction service should  check customer Credit card status must not be Stolen, Bankrupt or Charged off
to determine eligibility of credit card

Scenario: 1. Return eligibility status based on rule CR007
Given CR007 rules is configured
When deci is called with credit card product Arrangement having card status B
Then deci returns eligibility false
And Reason description for CR007

Scenario: 2. Return eligibility status based on rule CR007
Given CR007 rules is configured
When deci is called with credit card product Arrangement having card status Z
Then deci returns eligibility false
And Reason description for CR007

Scenario: 3. Return eligibility status based on rule CR007
Given CR007 rules is configured
When deci is called with credit card product Arrangement having card status U
Then deci returns eligibility false
And Reason description for CR007

Scenario: 4. Return eligibility status based on rule CR007
Given CR007 rules is configured
When deci is called with credit card product Arrangement having card status C
Then deci returns eligibility true

Scenario: 5. Return eligibility status based on rule CR007
Given CR007 rules is configured
When deci is called with credit card product Arrangement having card status null
Then deci returns eligibility true

Scenario: 6. Return eligibility status based on rule CR007
Given CR007 rules is configured
When deci is called with no credit card product Arrangement
Then deci returns eligibility false
And Reason description for CR007