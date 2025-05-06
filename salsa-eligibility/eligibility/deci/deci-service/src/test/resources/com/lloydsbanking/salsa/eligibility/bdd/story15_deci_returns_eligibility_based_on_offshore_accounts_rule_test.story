Meta:

Narrative:
Determine eligible customer instruction service should  check customer is having offshore accounts

Scenario: 1. Return eligibility status based on rule CR013

Given there is rule CR013 configured CR013 with threshold 5555 and channel is BBV0
When deci is called with deposit Arrangement having sort code 5555
Then deci returns eligibility false
And Reason description for rule CR013

Scenario: 2. Return eligibility status based on rule CR013

Given there is rule CR013 configured CR013 with threshold 5556 and channel is BBV1
When deci is called with deposit Arrangement having sort code 5555
Then deci returns eligibility true

Scenario: 3. Return eligibility status based on rule CR013

Given there is rule CR013 configured CR013 with threshold 5556:5557 and channel is BBV2
When deci is called with deposit Arrangement having sort code 5555
Then deci returns eligibility true

Scenario: 4. Return eligibility status based on rule CR013

Given there is rule CR013 configured CR013 with threshold 5556:5555 and channel is BBV3
When deci is called with deposit Arrangement having sort code 5555
Then deci returns eligibility false
And Reason description for rule CR013

Scenario: 5. Return eligibility status based on rule CR013

Given there is rule CR013 configured CR013 with threshold 5555:5556 and channel is BBV4
When deci is called with deposit Arrangement having sort code 5555
Then deci returns eligibility false
And Reason description for rule CR013

Scenario: 6. Return eligibility status based on rule CR013

Given there is rule CR013 configured CR013 with threshold 5555:5556 and channel is BBV4
When deci is called with no deposit Arrangement
Then deci returns eligibility false
And Reason description for rule CR013