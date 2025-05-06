Meta:

Narrative:
As the credit card journey I want OPACC to create customer record in PAM.

Scenario: 1 OPACC creates customer record in PAM DB and returns application Id and individual identifier

Given affiliate details are present
When UI calls OPACC with valid request
Then OPACC returns individual identifier and PAM appId
And OPACC saves 1 record in promo party applications table in PAM DB

Scenario: 2 OPACC creates customer record in PAM DB and saves promo party applications when affiliate details are present with affiliateNetworkAffiliateId and
matching record exists in PROMOTION_PARTY_EXT_SYSTEMS table for given affiliateNetworkAffiliateId

Given affiliate details are present with affiliateNetworkAffiliateId
And record exists in PROMOTION_PARTY_EXT_SYSTEMS table for given affiliateNetworkAffiliateId
When UI calls OPACC with valid request
Then OPACC returns individual identifier and PAM appId
And OPACC saves 2 record in promo party applications table in PAM DB

Scenario: 3 OPACC creates customer record in PAM DB and does not save promo party applications when affiliate details are present with affiliateNetworkAffiliateId and
no matching record is found in PROMOTION_PARTY_EXT_SYSTEMS table for given affiliateNetworkAffiliateId

Given affiliate details are present with affiliateNetworkAffiliateId
And no record exists in PROMOTION_PARTY_EXT_SYSTEMS table for given affiliateNetworkAffiliateId
When UI calls OPACC with valid request
Then OPACC returns individual identifier and PAM appId
And OPACC saves 0 record in promo party applications table in PAM DB