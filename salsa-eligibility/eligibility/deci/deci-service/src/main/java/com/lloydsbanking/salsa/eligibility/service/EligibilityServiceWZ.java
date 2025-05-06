package com.lloydsbanking.salsa.eligibility.service;

import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.logging.wz.EligibilityLogService;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPAMRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPRDRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.wz.EligibilityAnalyser;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductEligibilityTraceLog;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_bo.businessobjects.ResultCondition;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.IADetermineEligibleCustomerInstructions;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EligibilityServiceWZ implements IADetermineEligibleCustomerInstructions {
    private static final Logger LOGGER = Logger.getLogger(EligibilityServiceWZ.class);

    EligibilityLogService eligibilityLogServiceWZ;

    EligibilityPAMRetriever eligibilityPAMRetriever;

    HeaderRetriever headerRetrieverWZ;

    EligibilityPRDRetriever eligibilityPRDRetriever;

    RequestToResponseHeaderConverter requestToResponseHeaderConverter;

    EligibilityAnalyser eligibilityAnalyserWZ;

    EligibilityDataHelper eligibilityDataHelper;

    ExceptionUtility exceptionUtilityWz;

    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Autowired
    CustomerTraceLog customerTraceLog;

    @Autowired
    ProductEligibilityTraceLog productEligibilityTraceLog;

    @Autowired
    public EligibilityServiceWZ(EligibilityLogService eligibilityLogServiceWZ, HeaderRetriever headerRetrieverWZ, EligibilityPAMRetriever eligibilityPAMRetriever, EligibilityPRDRetriever eligibilityPRDRetriever, RequestToResponseHeaderConverter requestToResponseHeaderConverter, EligibilityAnalyser eligibilityAnalyserWZ, EligibilityDataHelper eligibilityDataHelper, ExceptionUtility exceptionUtilityWz) {
        this.eligibilityLogServiceWZ = eligibilityLogServiceWZ;
        this.headerRetrieverWZ = headerRetrieverWZ;
        this.eligibilityPAMRetriever = eligibilityPAMRetriever;
        this.eligibilityPRDRetriever = eligibilityPRDRetriever;
        this.requestToResponseHeaderConverter = requestToResponseHeaderConverter;
        this.eligibilityAnalyserWZ = eligibilityAnalyserWZ;
        this.eligibilityDataHelper = eligibilityDataHelper;
        this.exceptionUtilityWz = exceptionUtilityWz;
    }

    public EligibilityServiceWZ() {

    }

    @Override
    public DetermineEligibleCustomerInstructionsResponse determineEligibleCustomerInstructions(DetermineEligibleCustomerInstructionsRequest request) throws DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        eligibilityLogServiceWZ.initialiseContext(request.getHeader());
        LOGGER.info("Entering EligibilityServiceWZ " + customerTraceLog.getCustomerTraceEventMessage(request.getCustomerDetails(), "Entering DetermineEligibleCustomerInstructionWZ") + productArrangementTraceLog.getProdArrangementListTraceEventMessage(request.getExistingProductArrangments(), ""));
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        String channel = eligibilityPAMRetriever.getChannelIdFromContactPointId(headerRetrieverWZ.getContactPoint(request.getHeader()).getContactPointId(), request.getHeader());
        LOGGER.info("Channel fetched from Pam is :" + channel);
        request.getHeader().setChannelId(channel);
        //The list would be further passed for evaluating the rules which checks the existing product holdings
        List<ProductArrangement> existingProductArrangements = instructionDetailsOfExistingProductArrangements(request, channel);
        LOGGER.info("Instruction Mnemonic found for exiting products and size of list is :" + existingProductArrangements.size() + "and updated existing product arrangements are :" + productArrangementTraceLog.getProdArrangementListTraceEventMessage(request.getExistingProductArrangments(), ""));
        Map<String, Map<String, List<RefInstructionRulesPrdDto>>> conditionInstructionsMap = getCandidateInstructionMap(request, channel, request.getHeader());
        ResultCondition resultCondition = new ResultCondition();
        List<ProductEligibilityDetails> eligibilityDetails = getProductEligibilityDetailsList(conditionInstructionsMap, request, existingProductArrangements, channel, resultCondition);
        response.getProductEligibilityDetails().addAll(eligibilityDetails);
        //implementation pending for result condition.This would be done later.
        response.setResultCondition(resultCondition);
        response.setHeader(requestToResponseHeaderConverter.convert(request.getHeader()));
        eligibilityLogServiceWZ.clearContext();
        LOGGER.info("Exiting EligibilityServiceWZ " + productEligibilityTraceLog.getProductEligibiltyListTraceEventMessage(response.getProductEligibilityDetails(), ""));
        return response;
    }

    private List<ProductEligibilityDetails> getProductEligibilityDetailsList(Map<String, Map<String, List<RefInstructionRulesPrdDto>>> conditionInstructionsMap, DetermineEligibleCustomerInstructionsRequest request, List<ProductArrangement> existingProductArrangements, String channel, ResultCondition resultCondition) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        List<ProductEligibilityDetails> eligibilityDetails = new ArrayList<>();
        boolean isKycStatusAdded = false;
        for (Map.Entry conditionInstructionEntry : conditionInstructionsMap.entrySet()) {
            for (Map.Entry childInstructionEntry : conditionInstructionsMap.get(conditionInstructionEntry.getKey()).entrySet()) {
                ProductEligibilityDetails productEligibilityDetail = new ProductEligibilityDetails();
                Product product = new Product();
                InstructionDetails details = new InstructionDetails();
                String candidateInstruction = String.valueOf(childInstructionEntry.getKey());
                details.setInstructionMnemonic(candidateInstruction);
                product.setInstructionDetails(details);
                productEligibilityDetail.getProduct().add(product);
                productEligibilityDetail.setIsEligible(String.valueOf(false));
                List<ProductEligibilityDetails> productEligibilityDetailsList = new ArrayList<>();
                productEligibilityDetailsList.add(productEligibilityDetail);
                LOGGER.info("To call Eligibility Analyser for evaluating the rule for instructionMnemonic: " + candidateInstruction);
                //Invocation of analyser method of eligibilityAnalyser
                RequestHeader header = request.getHeader();
                List<RefInstructionRulesPrdDto> childInstructionEntryValue = (List<RefInstructionRulesPrdDto>) childInstructionEntry.getValue();
                XMLGregorianCalendar birthDate = (null != request.getCustomerDetails() && null != request.getCustomerDetails().getIsPlayedBy()) ? request.getCustomerDetails().getIsPlayedBy().getBirthDate() : null;
                eligibilityAnalyserWZ.checkEligibility(header, childInstructionEntryValue, birthDate, productEligibilityDetailsList, candidateInstruction, existingProductArrangements, eligibilityDataHelper.getSortCodeWZ(existingProductArrangements), eligibilityDataHelper.getCustomerIdWZ(request.getCustomerDetails()), request.getCustomerDetails(), request.getAssociatedProduct(), request.getArrangementType(), channel, resultCondition, request.getCandidateInstructions());
                for (ProductEligibilityDetails productEligibilityDetails : productEligibilityDetailsList) {
                    if (null != productEligibilityDetails.getKycStatus() && !isKycStatusAdded) {
                        eligibilityDetails.add(productEligibilityDetails);
                        isKycStatusAdded = true;
                    }
                }
                eligibilityDetails.add(productEligibilityDetailsList.get(0));
            }
        }
        return eligibilityDetails;
    }

    private Map<String, Map<String, List<RefInstructionRulesPrdDto>>> getCandidateInstructionMap(DetermineEligibleCustomerInstructionsRequest request, String channel, RequestHeader header) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        Map<String, Map<String, List<RefInstructionRulesPrdDto>>> conditionInstructionsMap = new HashMap<>();
        boolean isRuleAvailable = false;
        for (String customerInstruction : request.getCandidateInstructions()) {
            List<String> childInstructions;
            Map<String, List<RefInstructionRulesPrdDto>> instructionRulesCondition = new HashMap<>();
            childInstructions = fetchChildInstructions(customerInstruction, channel);
            for (String childInstruction : childInstructions) {
                List<RefInstructionRulesPrdDto> compositeInstructionConditions = eligibilityPRDRetriever.getCompositeInstructionConditions(childInstruction, channel, request.getHeader(), customerInstruction);
                if (!compositeInstructionConditions.isEmpty()) {
                    instructionRulesCondition.put(childInstruction, compositeInstructionConditions);
                    isRuleAvailable = true;
                }
            }
            conditionInstructionsMap.put(customerInstruction, instructionRulesCondition);
        }
        if (!isRuleAvailable) {
            throw exceptionUtilityWz.externalServiceError("816002", "No data found in PRD", header);
        }
        return conditionInstructionsMap;
    }

    private List<ProductArrangement> instructionDetailsOfExistingProductArrangements(DetermineEligibleCustomerInstructionsRequest request, String channel) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        List<ProductArrangement> productArrangements = request.getExistingProductArrangments();
        for (ProductArrangement productArrangement : productArrangements) {
            Product product = productArrangement.getAssociatedProduct();
            if (null != product && null != product.getExternalSystemProductIdentifier() && null != product.getProductIdentifier()) {
                String systemCd = null;
                if (!product.getExternalSystemProductIdentifier().isEmpty()) {
                    systemCd = product.getExternalSystemProductIdentifier().get(0).getSystemCode();
                }
                InstructionDetails instructionDetails = new InstructionDetails();
                String insMnemonic = eligibilityPRDRetriever.getChildInstructions(systemCd, product.getProductIdentifier(), product.getBrandName());
                if (null != insMnemonic) {
                    instructionDetails.setInstructionMnemonic(insMnemonic);
                    instructionDetails.setParentInstructionMnemonic(eligibilityPRDRetriever.getParentInstruction(insMnemonic, channel, request.getHeader()));
                }
                productArrangement.getAssociatedProduct().setInstructionDetails(instructionDetails);
            }
        }
        return productArrangements;
    }

    private List<String> fetchChildInstructions(String customerInstruction, String channel) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        List<String> childInstructionsList = new ArrayList();
        if (Mnemonics.GROUP_ISA.equals(customerInstruction)) {
            childInstructionsList.addAll(eligibilityPRDRetriever.retrieveInstructionsHierarchyForGrndPrnt(customerInstruction, channel));
        }
        else {
            childInstructionsList.addAll(eligibilityPRDRetriever.retrieveChildInstructionsHierarchy(customerInstruction, channel));
        }
        if (childInstructionsList.isEmpty()) {
            childInstructionsList.add(customerInstruction);
        }
        return childInstructionsList;
    }
}

