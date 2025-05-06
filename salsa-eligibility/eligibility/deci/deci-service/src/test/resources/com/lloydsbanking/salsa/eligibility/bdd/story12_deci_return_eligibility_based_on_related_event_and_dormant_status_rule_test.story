Meta:

Narrative:
Determine eligible customer instruction service should  check Customer doesn't have an existing product with the threshold enabled


Scenario: 1. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR004 with threshold 37 and channel is TBV
When deci is called with product Arrangement having related event 37 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR004 and threshold 37

Scenario: 2. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR004 with threshold 37 and channel is TBV
When deci is called with product Arrangement having related event 37 and status Effective
Then deci returns eligibility true

Scenario: 3. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR004 with threshold 37 and channel is TBV
When deci is called with product Arrangement having related event 40 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR004 and threshold 37

Scenario: 4. Return eligibility status based on rule CR004 CR008 CR009 CR010(need to verify
Given there is rule CR004 with threshold null and channel is STL
When deci is called with product Arrangement having related event 40 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR004 and threshold null

Scenario: 5. Return eligibility status based on rule CR004 CR008 CR009 CR010
Given there is rule CR004 with threshold 37 and channel is TBV
When deci is called with product Arrangement having related event null and status Dormant
Then deci returns eligibility false
And Reason description for rule CR004 and threshold 37

Scenario: 6. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR008 with threshold 37 and channel is IBV
When deci is called with product Arrangement having related event 37 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR008 and threshold 37

Scenario: 7. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR008 with threshold 37 and channel is IBV
When deci is called with product Arrangement having related event 37 and status Effective
Then deci returns eligibility true

Scenario: 8. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR008 with threshold 37 and channel is IBV
When deci is called with product Arrangement having related event 40 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR008 and threshold 37

Scenario: 9. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR008 with threshold null and channel is STV
When deci is called with product Arrangement having related event 40 and status Dormant
Then deci returns eligibility false
And Reason description for rule CR008 and threshold null

Scenario: 10. Return eligibility status based on rule CR004 CR008 CR009 CR010

Given there is rule CR008 with threshold 37 and channel is IBV
When deci is called with product Arrangement having related event null and status Dormant
Then deci returns eligibility false
And Reason description for rule CR008 and threshold 37

Scenario: 11. Return eligibility status based on rule CR005

Given there is rule CR005 with threshold Dormant and channel is IBH
When deci is called with product Arrangement status Dormant
Then deci returns eligibility false
And Reason description for rule CR005 and threshold Dormant

Scenario: 12. Return eligibility status based on rule CR005

Given there is rule CR005 with threshold Dormant and channel is IBH
When deci is called with product Arrangement status Effective
Then deci returns eligibility true

Scenario: 13. Return eligibility status based on rule CR005

Given there is rule CR005 with threshold Dormant and channel is IBH
When deci is called with product Arrangement status null
Then deci returns eligibility false
And Reason description for rule CR005 and threshold Dormant

Scenario: 14. Return eligibility status based on rule CR005

Given there is rule CR005 with threshold null and channel is STS
When deci is called with product Arrangement status null
Then deci returns eligibility false
And Reason description for rule CR005 and threshold null

Scenario: 15. Return eligibility status based on rule CR005

Given there is rule CR005 with threshold null and channel is IBS
When deci is called with product Arrangement status Dormant
Then deci returns eligibility true