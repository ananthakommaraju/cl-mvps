Meta:
@BusinessRule GIBBR585

Narrative:
OfferProductArrangementSaving responds checks for Duplicate application

Scenario: 1 OPASaving responds with duplicate application error when customer has existing Easy Saver application in Approved status
Meta:
@TCM 13
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|Easy Saver         |Approved|


Scenario: 2 OPASaving responds with duplicate application error when customer has existing ISA Saver Variable application in referred status
Meta:
@TCM 14
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|ISA Saver Variable |Referred|


Scenario: 3 OPASaving responds to with duplicate application error when customer has existing Fixed Rate Bond application in Awaiting Referral Processing status
Meta:
@TCM 15
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|Fixed Rate Bond 2yr|Awaiting Referral Processing|


Scenario: 4 OPASaving responds to with duplicate application error when customer has existing eSavings application in Awaiting Fulfilment status
Meta:
@TCM 16
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|eSavings           |Awaiting Fulfilment|


Scenario: 5 OPASaving responds to with duplicate application error when customer has existing Cash ISA application in Initialised status
Meta:
@TCM 17
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|Cash ISA           |Initialised|


Scenario: 6 OPASaving responds to with duplicate application error when customer has existing Tracker Bond application in Referral Processed status
Meta:
@TCM 18
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|Tracker Bond       |Referral Processed|


Scenario: 7 OPASaving responds to with duplicate application error when customer has existing Cash ISA application in Unscored status
Meta:
@TCM 19
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OPASaving returns duplicate application error in response
And OpaSaving returns no application status in the response
Examples:
|product            |status |
|Cash ISA           |Unscored|


Scenario: 8 OPASaving responds with proper response when no duplicate application exists
Given No duplicate applications exists in PAM
When UI calls OPASaving with valid request
Then OPASaving returns valid response


Scenario: 9 OPASaving responds to normally when there is a duplicate application with Decline status
Meta:
@TCM 23
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OpaSaving returns application status as Approved for the customer
And OpaSaving returns EIDV status as accept for the customer
And OpaSaving returns ASM score as accept for the customer
Examples:
|product            |status |
|eSavings           |Declined|


Scenario: 10 OPASaving responds with proper response when customer has existing Easy Saver application in Abandonded status
Meta:
@TCM 20
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OpaSaving returns application status as Approved for the customer
And OpaSaving returns EIDV status as accept for the customer
And OpaSaving returns ASM score as accept for the customer
Examples:
|product             |status |
|Easy Saver          |Abandoned|


Scenario: 11 OPASaving responds with proper response when customer has existing ISA application in Fulfiled status
Meta:
@TCM 21
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OpaSaving returns application status as Approved for the customer
And OpaSaving returns EIDV status as accept for the customer
And OpaSaving returns ASM score as accept for the customer
Examples:
|product             |status |
|Cash ISA            |Fulfilled|


Scenario: 12 OPASaving responds with proper response when customer has existing Term Deposit application in Cancelled status
Meta:
@TCM 22
Given Duplicate applications for a <product> product exists in PAM with a status of <status>
And applications status is not ASM decline
When UI calls OPASaving with valid request
Then OpaSaving returns application status as Approved for the customer
And OpaSaving returns EIDV status as accept for the customer
And OpaSaving returns ASM score as accept for the customer
Examples:
|product             |status |
|Fixed Rate Bond 2yr |Cancelled|

