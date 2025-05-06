Meta:

Narrative:
OfferProductArrangementPCC determines eligibility of the applied product

Scenario: 1 OPACC returns application type as 10001 for unauth customer

Given Customer is unauth customer
And multi card switch is enabled
And eligibility is true
And number of credit card holds of same brand is zero
And application type is not present in request
When UI calls OPACC with valid request
Then OPACC returns application type as 10001

Scenario: 2 OPACC returns application type same as request for unauth customer

Given Customer is unauth customer
And multi card switch is enabled
And eligibility is true
And number of credit card holds of same brand is zero
And application type is present in request
When UI calls OPACC with valid request
Then OPACC returns application type same as request

Scenario: 3 OPACC returns application type as ineligible for unauth customer

Given Customer is unauth customer
And multi card switch is enabled
And eligibility is false
When UI calls OPACC with valid request
Then OPACC returns application type as ineligible

Scenario: 4 OPACC returns application type as 10001 for unauth customer

Given Customer is unauth customer
And multi card switch is enabled
And eligibility is true
And number of credit card holds of same brand is greater than or equal to one
And administerservice returns product eligibility type as co_hold
When UI calls OPACC with valid request
Then OPACC returns application type as 10001


Scenario: 5  OPACC returns application type as ineligible for unauth customer

Given Customer is unauth customer
And multi card switch is enabled
And eligibility is true
And number of credit card holds of same brand is greater than or equal to one
And administerservice returns product eligibility type as ineligible
When UI calls OPACC with valid request
Then OPACC returns application type as ineligible

Scenario: 6 OPACC returns application type as ineligible for unauth customer

Given Customer is unauth customer
And multi card switch is not enabled
And number of credit card holds of same brand is greater than or equal to one
When UI calls OPACC with valid request
Then OPACC returns application type as ineligible

Scenario: 7 OPACC returns application type as 10001 for unauth customer

Given Customer is unauth customer
And multi card switch is not enabled
And number of credit card holds of same brand is zero
And application type is not present in request
When UI calls OPACC with valid request
Then OPACC returns application type as 10001

Scenario: 8 OPACC returns application type same as request for unauth customer

Given Customer is unauth customer
And multi card switch is not enabled
And number of credit card holds of same brand is zero
And application type is present in request
When UI calls OPACC with valid request
Then OPACC returns application type same as request

Scenario: 9 OPACC returns application type as 10001 for auth customer

Given Customer is auth customer
And number of credit card holds of same brand is zero
And application type is not present in request
When UI calls OPACC with valid request
Then OPACC returns application type as 10001

Scenario: 10 OPACC returns application type same as request for auth customer

Given Customer is auth customer
And number of credit card holds of same brand is zero
And application type is present in request
When UI calls OPACC with valid request
Then OPACC returns application type same as request

Scenario: 11 OPACC returns application type as 10001 for auth customer

Given Customer is auth customer
And number of credit card holds of same brand is zero
And application type is not present in request
When UI calls OPACC with valid request
Then OPACC returns application type as 10001

Scenario: 12 OPACC returns application type as 10001 for auth customer

Given Customer is auth customer
And number of credit card holds of same brand is greater than or equal to one
And administerservice returns product eligibility type as co_hold
When UI calls OPACC with valid request
Then OPACC returns application type as 10001

Scenario: 13 OPACC returns application type as ineligible for auth customer

Given Customer is auth customer
And number of credit card holds of same brand is greater than or equal to one
And administerservice returns product eligibility type as ineligible
When UI calls OPACC with valid request
Then OPACC returns application type as ineligible





