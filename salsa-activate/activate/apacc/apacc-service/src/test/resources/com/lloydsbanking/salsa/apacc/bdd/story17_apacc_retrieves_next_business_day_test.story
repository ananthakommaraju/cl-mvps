Meta:

Narrative:
Activate Product Arrangement updates application details for balance transfer and retrieves next business day

Scenario: 1. APACC updates status and retrieves next business day when balance transfer is present
!--Given balance transfer is present
!--When there is a call to APACC
!--Then APACC updates status to awaiting post fulfilment process and retrieves next business day

Scenario: 2. APACC updates status when balance transfer is not present
Given balance transfer is not present
When there is a call to APACC
Then APACC updates status to fulfilled process

Scenario: 3. APACC throws error
!--Given balance transfer is present and B748 throws error
!--When there is a call to APACC
!--Then APACC service continues






