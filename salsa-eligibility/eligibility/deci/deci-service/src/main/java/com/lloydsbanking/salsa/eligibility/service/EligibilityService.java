package com.lloydsbanking.salsa.eligibility.service;

import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.logging.EligibilityLogService;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityRefDataRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.EligibilityAnalyser;
import com.lloydsbanking.salsa.eligibility.service.rules.RBBSEligibilityAnalyser;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityDataHelper;
import com.lloydsbanking.salsa.eligibility.service.validator.RequestValidator;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.IADetermineEligibleCustomerInstructions;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EligibilityService implements IADetermineEligibleCustomerInstructions {
    private static final Logger LOGGER = Logger.getLogger(EligibilityService.class);

    private static Set<String> rBBSProducts = new HashSet(Arrays.asList("P_BOD_RBB", "P_BODA_RBB", "P_BLN_RBB"));

    EligibilityLogService eligibilityLogService;

    HeaderRetriever headerRetriever;

    RequestValidator requestValidator;

    EligibilityRefDataRetriever eligibilityRefDataRetriever;

    EligibilityAnalyser eligibilityAnalyser;

    RBBSEligibilityAnalyser rBBSEligibilityAnalyser;

    RequestToResponseHeaderConverter requestToResponseHeaderConverter;

    EligibilityDataHelper eligibilityDataHelper;

    @Autowired
    public EligibilityService(final EligibilityLogService eligibilityLogService, HeaderRetriever headerRetriever, RequestValidator requestValidator, EligibilityRefDataRetriever eligibilityRefDataRetriever, EligibilityAnalyser eligibilityAnalyser, RBBSEligibilityAnalyser rBBSEligibilityAnalyser, RequestToResponseHeaderConverter requestToResponseHeaderConverter, EligibilityDataHelper eligibilityDataHelper) {
        this.eligibilityLogService = eligibilityLogService;
        this.headerRetriever = headerRetriever;
        this.requestValidator = requestValidator;
        this.eligibilityRefDataRetriever = eligibilityRefDataRetriever;
        this.eligibilityAnalyser = eligibilityAnalyser;
        this.rBBSEligibilityAnalyser = rBBSEligibilityAnalyser;
        this.requestToResponseHeaderConverter = requestToResponseHeaderConverter;
        this.eligibilityDataHelper = eligibilityDataHelper;
    }

    public EligibilityService() {
    }

    @Override
    public DetermineElegibleInstructionsResponse determineEligibleInstructions(DetermineElegibileInstructionsRequest request) throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsExternalBusinessErrorMsg {

        eligibilityLogService.initialiseContext(request.getHeader());
        LOGGER.info("Entering determineEligibleInstructions BZ");

        DetermineElegibleInstructionsResponse response = new DetermineElegibleInstructionsResponse();
        requestValidator.validateRequest(request);

        String channel = eligibilityRefDataRetriever.getChannelIdFromContactPointId(headerRetriever.getContactPoint(request.getHeader()).getContactPointId(), request.getHeader());
        LOGGER.info("Channel: " + channel + " Size of customerArrangements List: " + (null != request.getCustomerArrangements() ? request.getCustomerArrangements().size() : "null") + " Size of businessArrangements list: " + (null != request.getBusinessArrangements() ? request.getBusinessArrangements().size() : "null"));
        request.getHeader().setChannelId(headerRetriever.getChannelId(request.getHeader()));

        boolean isRBBSproduct = isRBBSProduct(request.getCandidateInstructions());

        List<String> childInstructions = generateChildInstructionList(request, channel, isRBBSproduct);
        List<CustomerInstruction> checkedInstructions = evaluateRules(request, channel, childInstructions, isRBBSproduct);
        response.getCustomerInstructions().addAll(checkedInstructions);

        prioritisedInstructions(response.getCustomerInstructions());
        response.setHeader(requestToResponseHeaderConverter.convert(request.getHeader()));
        eligibilityLogService.clearContext();
        LOGGER.info("Exiting determineEligibleInstructions BZ");
        return response;

    }

    private List<String> generateChildInstructionList(DetermineElegibileInstructionsRequest request, String channel, boolean isRBBSproduct) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        List<String> childInstructionList = new ArrayList();
        if (!isRBBSproduct) {
            for (ProductArrangement customerArrangement : request.getCustomerArrangements()) {
                String productInsMnemonic = fetchProductArrangementInstruction(customerArrangement, channel);
                if (null != productInsMnemonic) {
                    customerArrangement.setInstructionMnemonic(productInsMnemonic);
                    customerArrangement.setParentInstructionMnemonic(fetchParentInstruction(productInsMnemonic, channel, request));
                }
            }
            for (String candidateInstructionForChild : request.getCandidateInstructions()) {
                List<String> childInstructions = fetchChildInstruction(candidateInstructionForChild, channel);
                if (null != childInstructions && !childInstructions.isEmpty()) {
                    childInstructionList.addAll(childInstructions);
                }
                else {
                    childInstructionList.add(candidateInstructionForChild);
                }
            }
        }
        else {
            childInstructionList.addAll(request.getCandidateInstructions());
        }
        return childInstructionList;
    }

    private List<CustomerInstruction> evaluateRules(DetermineElegibileInstructionsRequest request, String channel, List<String> childInstructionList, boolean isRBBSproduct) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {
        List<CustomerInstruction> instructions = new ArrayList<>();
        for (String childInstruction : childInstructionList) {
            LOGGER.info("Evaluating eligibility for childInstruction: " + childInstruction);
            List<RefInstructionRulesDto> refInstructionRulesDtos = fetchCompositeInstructionsRule(childInstruction, channel, request);
            CustomerInstruction customerInstruction = new CustomerInstruction();
            customerInstruction.setEligibilityIndicator(false);
            if (isRBBSproduct) {
                rBBSEligibilityAnalyser.checkEligibilityForRBBSProducts(request.getHeader(), refInstructionRulesDtos,
                    null != request.getIndividual().getBirthDate() ? request.getIndividual().getBirthDate() : null,
                    eligibilityDataHelper.getSortCode(request.getCustomerArrangements()),
                    eligibilityDataHelper.getCustomerId(request.getCustomerArrangements()),
                    customerInstruction, request.getCustomerArrangements(), request.getSelctdArr(), childInstruction,
                    request.getBusinessArrangements(), request.getSelctdBusnsId());
            }
            else {
                eligibilityAnalyser.checkEligibility(request.getHeader(), refInstructionRulesDtos,
                    null != request.getIndividual().getBirthDate() ? request.getIndividual().getBirthDate() : null,
                    eligibilityDataHelper.getSortCode(request.getCustomerArrangements()),
                    eligibilityDataHelper.getCustomerId(request.getCustomerArrangements()), customerInstruction,
                    request.getCustomerArrangements(), request.getSelctdArr(), childInstruction, request.getBusinessArrangements(),
                    request.getSelctdBusnsId());
            }
            customerInstruction.setInstructionMnemonic(childInstruction);
            if (!isRBBSproduct) {
                customerInstruction.setPriority(eligibilityRefDataRetriever.getInstructionPriority(childInstruction, channel));
            }
            instructions.add(customerInstruction);
        }

        return instructions;
    }

    private String fetchParentInstruction(String candidateInstruction, String channel, DetermineElegibileInstructionsRequest request) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        return eligibilityRefDataRetriever.getParentInstruction(candidateInstruction, channel, request.getHeader());
    }

    private String fetchProductArrangementInstruction(ProductArrangement productArrangement, String channel) {
        String accountHost = productArrangement.getAccountType().substring(0, 1);
        String accountType = productArrangement.getAccountType().substring(1);
        return eligibilityRefDataRetriever.getProductArrangementInstruction(accountHost, accountType, channel);
    }

    private List<String> fetchChildInstruction(String candidateInstruction, String channel) {
        return eligibilityRefDataRetriever.getChildInstructions(candidateInstruction, channel);
    }

    private List<RefInstructionRulesDto> fetchCompositeInstructionsRule(String candidateInstruction, String channel, DetermineElegibileInstructionsRequest request) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        return eligibilityRefDataRetriever.getCompositeInstructionCondition(candidateInstruction, channel, request.getHeader());
    }

    public void prioritisedInstructions(List<CustomerInstruction> customerInstructions) {
        Collections.sort(customerInstructions, new Comparator() {
            public int compare(Object customerInstructionOne, Object customerInstructionTwo) {
                return ((CustomerInstruction) customerInstructionOne).getPriority().compareTo(((CustomerInstruction) customerInstructionTwo).getPriority());
            }
        });
    }

    private boolean isRBBSProduct(List<String> candidateInstructions) {
        if (!candidateInstructions.isEmpty() && candidateInstructions.size() == 1 && rBBSProducts.contains(candidateInstructions.get(0))) {
            LOGGER.info("isRBBSProduct: true");
            return true;
        }
        return false;
    }
}