package com.lloydsbanking.salsa.opaloans.service;


import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;

import java.util.List;

public interface ScenarioHelper {
    public void expectF336Call(RequestHeader header, String partyIdentifier);

    public void expectF061Call(RequestHeader header, String customerIdentifier, String partyIdentifier);

    public void expectF061CallForNonKycCompliance(RequestHeader header, String customerIdentifier, String partyIdentifier);

    public void expectB231Call(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, int errorNo) throws ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;

    public void expectB231CallWithoutEligibleProducts(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, int errorNo) throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;

    public void expectB237Call(RequestHeader header, String customerIdentifier, String partyIdentifier, String duplicateStatus, int errorNo) throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo, ErrorInfo;

    public void expectEligibilityCall(RequestHeader header, String customerIdentifier, String eligibilityStatus, String code, String description) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;

    public void expectLRASwitchCall(String channel, String switchStatus);

    public void expectVerdeSwitchCall(String channel, String switchStatus);

    public void expectLookupDataForLegalEntity(String channel, String groupCode);

    public void expectGetChannelIdFromContactPointId(String contactPointId);

    public void expectC216CallForUniqueDateOfBirth(RequestHeader header, String sortCode, String accountNumber, long partyId);

    public void expectC216CallForBirthDateNotMatched(RequestHeader header, String sortCode, String accountNumber, String additionalDataIndicator, long partyId);

    public void clearUp();

    public void expectPAMReferenceData();

    public void expectPrdDbCalls(String insMnemonic, String brand, String rule);

    public void expectE141Call(RequestHeader header, List<Integer> indicators, String sortCode, String accNo, String maxLimitAmt);

    public void expectB695Call(RequestHeader header, String accType, String eventType) throws com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo ;

    public int expectApplicationsCreated();

    public int expectIndividualsCreated();
}
