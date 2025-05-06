Meta:

Narrative:
Activate Product Arrangement generates document

Scenario: 1. APACC sets isStoreApplication and isTradeApplication flag when generate document succeeds
Given generates document succeeds
When there is a call to APACC
Then set isStoreApplication and isTradeApplication flag and service continues

Scenario: 2. APACC sets substatus as ACQUIRE_CALL_FAILURE when generate document fails
!--Given generate document fails
!--When there is a call to APACC
!--Then assign Application SubStatus as ACQUIRE_CALL_FAILURE