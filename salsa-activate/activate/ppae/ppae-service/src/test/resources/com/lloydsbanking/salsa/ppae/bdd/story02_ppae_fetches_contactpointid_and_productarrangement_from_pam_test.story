Meta:

Narrative:
Process pending Arrangement Event Service Fetches ContactPointID and ProductArrangement

Scenario: 1. PPAE Fetches ContactPointId from PAM Database
Given ChannelID is mapped in PAM database
When There is call to PPAE with valid request
Then PPAE service fetches ContactPointId


Scenario: 2. PPAE service throws error for unavailable channelId
Given ChannelID is not mapped in PAM database
When There is call to PPAE with valid request
Then PPAE returns error and throws exception as dataNotAvailable


Scenario: 3. PPAE retrieve  product arrangement details from pam for related application id
Given request contains application id
When There is call to PPAE with valid request
Then service retrieves ProductArrangement