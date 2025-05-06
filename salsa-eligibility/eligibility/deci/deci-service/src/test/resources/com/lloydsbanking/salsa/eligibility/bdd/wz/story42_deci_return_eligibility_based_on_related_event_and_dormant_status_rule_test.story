Meta:

Narrative:
Determine eligible customer instruction service should  check Customer doesn't have an existing product with the threshold enabled


Scenario: 1. Return eligibility status based on rule CR004

Given there is rule CR004 with threshold 37 and channel is LTB
When deci is called with product Arrangement having related event 37 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR004 and threshold 37

Scenario: 2. Return eligibility status based on rule CR004

Given there is rule CR004 with threshold 37 and channel is LTB
When deci is called with product Arrangement having related event 37 and status Effective
Then deci returns eligibility true

Scenario: 3. Return eligibility status based on rule CR004

Given there is rule CR004 with threshold 37 and channel is LTB
When deci is called with product Arrangement having related event 30 and status Effective
Then deci returns eligibility false
And Reason description for rule CR004 and threshold 37



