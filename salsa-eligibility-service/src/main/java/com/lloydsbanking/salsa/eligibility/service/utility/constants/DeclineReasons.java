package com.lloydsbanking.salsa.eligibility.service.utility.constants;

public interface DeclineReasons {
    String CR026_DECLINE_REASON = "KnowYourCustomer check not complete";

    String CR022_DECLINE_REASON = "Customer has current account and Shadow Limit amount is 0";

    String CR024_DECLINE_REASON = "Customer doesn\u0092t have current account of logged in Channel and  has a no loan";

    String CR025_DECLINE_REASON = "Customer doesn’t have current account of logged in channel but has a loan";

    String CR027_DECLINE_REASON = "Customer doesn’t have current account of logged in channel";

    String CR031_DECLINE_REASON = "Customer has 2 or more credit cards";

    String CR007_DECLINE_REASON = "Credit card status must not be Stolen, Bankrupt or Charged off";

    String CR013_DECLINE_REASON = "Customer only has off-shore accounts";

    String CR058_DECLINE_REASON = "Customer has strict flag set on one or more of the current account holding(s)";

    String CR059_DECLINE_REASON_1 = "Customer has CBS indicator ";

    String CR059_DECLINE_REASON_2 = " set on one of the current account holding(s)";

    String CR061_DECLINE_REASON_1 = "Customer has indicator ";

    String CR061_DECLINE_REASON_2 = " set";

    String CR056_DECLINE_REASON_1 = "Customer has indicator ";

    String CR056_DECLINE_REASON_2 = " for selected account";

    String CR006_DECLINE_REASON = "Customer cannot apply for a product of the same type as they already have.";

    String CR011_DECLINE_REASON = "Customer doesn't have any eligible credit card accounts for Balance transfer";

    String CR012_DECLINE_REASON = "Customer does not have an eligible product for PPC";

    String CR060_DECLINE_REASON = "Customer does not hold an active current account having DirectDebit and shadow limit greater than 0.";

    String CR015_DECLINE_REASON = "Funds have been deposited this year";

    String CR016_DECLINE_REASON = "ISA opened this year";

    String CR052_DECLINE_REASON = "Customer has MBC role.";

    String CR028_DECLINE_REASON = "Customer cannot have more than 5 instances of the product.";

    String CR051_DECLINE_REASON_1 = "Business Entity Type ";

    String CR051_DECLINE_REASON_2 = " is not in allowed list ";

    String CR023_DECLINE_REASON = "Customer has current account and Shadow Limit amount is less than threshold";

    String CR038_DECLINE_REASON = "Customer doesn't have an active current account ";

    String CR049_DECLINE_REASON = "Customer does not hold Club Account";

    String CR035_DECLINE_REASON = "Maximum number of fixed rate ISA\u0092s held";

    String CR047_DECLINE_REASON = "Address Validation Failed";

    String CR044_DECLINE_REASON = "Customer has a Dormant Account ";

    String CR032_DECLINE_REASON_NO_AVA_ACCOUNT = "Customer does not have AVA Account";

    String CR032_DECLINE_REASON_NO_ARRANGEMENTS = "Customer does not have any Account";

    String CR036_DECLINE_REASON = "Customer doesn't have a current account";

    String CR045_DECLINE_REASON = "Joint Signature not Present.";

    String CR065_DECLINE_REASON = "Name or Address Validation Failed";

    String CR033_DECLINE_REASON_NO_LTSB_ACCOUNT = "Customer does not have LTSB account";

    String CR046_DECLINE_REASON = "Post returned from this address";

    String CR067_DECLINE_REASON = "Customer has invalid shadowLimit";

    String CR068_DECLINE_REASON = "Customer has all Joint Signatory Accounts";

    String CR039_DECLINE_REASON = "nationality is not allowed";

    String CR062_DECLINE_REASON = "Customer has invalid decision code";

    String CR048_DECLINE_REASON = "Customer not eligible to apply for a credit card";

    String CR041_DECLINE_REASON = "Customer have CBS accounts with indicators ";

    String CR069_DECLINE_REASON = "Funds deposited within the same tax year.";

    String CR034_DECLINE_REASON = "Customer does not have a current account";

    String CR066_DECLINE_REASON = "Customer fails UK residency check";
    String CR063_DECLINE_REASON = "Customer already has fulfilled finance application";
}
