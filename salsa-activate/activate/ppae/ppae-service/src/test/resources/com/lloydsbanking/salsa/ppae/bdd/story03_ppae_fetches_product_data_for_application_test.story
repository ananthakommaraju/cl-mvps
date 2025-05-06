Meta:


Product Pending Arrangement Fetches Product Details for Application Status Awaiting Rescore


Scenario: 1. PPAE Fetches Product Details for Application Status Awaiting Rescore
Given Application status as awaiting rescore
And There is a internal call to RetrieveProductConditions with input
When There is a call to PPAE
Then PPAE fetches Product Conditions successfully
