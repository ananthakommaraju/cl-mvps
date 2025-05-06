package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionRequest;
import com.lloydstsb.schema.enterprise.lcsm.CurrencyAmount;
import com.lloydstsb.schema.enterprise.lcsm_accountingtransaction.Inpayment;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementAssociation;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementType;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.MerchantSchemeArrangement;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductAccessArrangement;
import com.lloydstsb.schema.enterprise.lcsm_authorization.Service;
import com.lloydstsb.schema.enterprise.lcsm_channel.Channel;
import com.lloydstsb.schema.enterprise.lcsm_common.*;
import com.lloydstsb.schema.enterprise.lcsm_communication.Communication;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRole;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRoleType;
import com.lloydstsb.schema.enterprise.lcsm_product.ProductGroup;
import com.lloydstsb.schema.enterprise.lcsm_product.ProductGroupSalesAttributes;
import com.lloydstsb.schema.enterprise.lcsm_resourceitem.FinancialTransactionCard;
import lib_sim_bo.businessobjects.BalanceTransfer;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class IssueInPaymentInstRequestFactory {

    private static final String SOURCE_LOGICAL_ID = "H";
    private static final String CARD_NUMBER = "CARD_NUMBER";
    private static final String CREDIT_CARD = "CREDIT_CARD";
    private static final String DESTINATION_BANK_ACCOUNT_EXTERNAL_IDENTIFIER = "DESTINATION_BANK_ACCOUNT_EXTERNAL_IDENTIFIER";
    private static final String DESTINATION_SORT_CODE = "DESTINATION_SORT_CODE";
    private static final String FINANCIAL_INSTITUTION = "FINANCIAL_INSTITUTION";
    private static final String MAINTENANCE_AUDIT_DATA = "R8V1";
    private static final String MAINTENANCE_AUDIT_TYPE = "Message Version Number";
    private static final String CONDITION_NAME = "MAXIMUM_REPEAT_GROUP_QUANTITY";
    private static final String CONDITION_TYPE_NAME = "UsageCondition";
    private static final String CONDITION_RESULT = "0";
    private static final String CURRENCY_CODE = "GBP";
    private static final String CHANNEL_NAME = "01";
    private static final String CARD_OFFER_CODE_NAME = "Card Offer Code";

    public IssueInpaymentInstructionRequest convert(String btOffAttributeValue, BalanceTransfer balanceTransfer, String sourceCreditCard) {
        IssueInpaymentInstructionRequest request = new IssueInpaymentInstructionRequest();
        request.setSourceArrangement(getSourceArrangement(sourceCreditCard));
        request.setBeneficiaryRequestDetails(getBeneficiaryRequestDetails(btOffAttributeValue, balanceTransfer));
        return request;
    }

    private Inpayment getBeneficiaryRequestDetails(String btOffAttributeValue, BalanceTransfer balanceTransfer) {
        Inpayment inpayment = new Inpayment();
        inpayment.setTargetArrangement(getTargetArrangement(balanceTransfer));

        CurrencyAmount currencyAmount = new CurrencyAmount();
        if (balanceTransfer.getAmount() != null) {
            currencyAmount.setTheCurrencyAmount(balanceTransfer.getAmount().getAmount());
        }
        currencyAmount.setTheCurrencyCode(CURRENCY_CODE);
        inpayment.setAmount(currencyAmount);

        inpayment.setService(getService(btOffAttributeValue));
        inpayment.setSourceEvent(getSourceEvent());
        return inpayment;
    }

    private com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement getTargetArrangement(BalanceTransfer balanceTransfer) {
        com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement targetArrangement = new com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement();
        if (!StringUtils.isEmpty(balanceTransfer.getCreditCardNumber())) {
            ArrangementType arrangementType = new ArrangementType();
            arrangementType.setName(CREDIT_CARD);
            targetArrangement.setHasArrangementType(arrangementType);
            targetArrangement.getArrangementAssociations().add(getArrangementAssociation(balanceTransfer.getCreditCardNumber()));
        }
        if (null != balanceTransfer.getCurrentAccountDetails() && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getSortCode()) && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getAccountNumber())) {
            targetArrangement.setObjectReference(getObjectReference(balanceTransfer));
            targetArrangement.getRoles().add(getInvolvedPartyRole(balanceTransfer));
        }
        return targetArrangement;
    }


    private ObjectReference getObjectReference(BalanceTransfer balanceTransfer) {
        ObjectReference objectReference = new ObjectReference();
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString(DESTINATION_BANK_ACCOUNT_EXTERNAL_IDENTIFIER);
        alternateId.setValue(balanceTransfer.getCurrentAccountDetails().getAccountNumber());
        objectReference.getAlternateId().add(alternateId);
        return objectReference;
    }

    private InvolvedPartyRole getInvolvedPartyRole(BalanceTransfer balanceTransfer) {
        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        ObjectReference sortCodeObjectReference = new ObjectReference();
        AlternateId sortCodeAlternateId = new AlternateId();
        sortCodeAlternateId.setAttributeString(DESTINATION_SORT_CODE);
        sortCodeAlternateId.setValue(balanceTransfer.getCurrentAccountDetails().getSortCode());
        sortCodeObjectReference.getAlternateId().add(sortCodeAlternateId);
        involvedPartyRole.setObjectReference(sortCodeObjectReference);
        InvolvedPartyRoleType involvedPartyRoleType = new InvolvedPartyRoleType();
        involvedPartyRoleType.setValue(FINANCIAL_INSTITUTION);
        involvedPartyRole.setType(involvedPartyRoleType);
        return involvedPartyRole;
    }

    private Service getService(String btOffAttributeValue) {
        Service service = new Service();
        Channel channel = new Channel();
        channel.setName(CHANNEL_NAME);
        service.getChannels().add(channel);
        ProductGroup productGroup = new ProductGroup();
        productGroup.getHasSalesAttributes().add(new ProductGroupSalesAttributes());
        productGroup.getHasSalesAttributes().get(0).setName(CARD_OFFER_CODE_NAME);
        productGroup.getHasSalesAttributes().get(0).setValue(btOffAttributeValue);
        service.getProductGroups().add(productGroup);
        return service;
    }

    private MerchantSchemeArrangement getSourceArrangement(String sourceCreditCardNumber) {
        MerchantSchemeArrangement sourceArrangement = new MerchantSchemeArrangement();
        sourceArrangement.setObjectReference(new ObjectReference());
        sourceArrangement.getObjectReference().getAlternateId().add(new AlternateId());
        sourceArrangement.getObjectReference().getAlternateId().get(0).setAttributeString(CARD_NUMBER);
        sourceArrangement.getObjectReference().getAlternateId().get(0).setValue(sourceCreditCardNumber);
        return sourceArrangement;
    }

    private ArrangementAssociation getArrangementAssociation(String creditCardNumber) {
        ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
        ProductAccessArrangement relatedArrangement = new ProductAccessArrangement();
        FinancialTransactionCard hasCards = new FinancialTransactionCard();
        ObjectReference objectReference = new ObjectReference();
        AlternateId alternateId = new AlternateId();
        alternateId.setValue(creditCardNumber);
        alternateId.setSourceLogicalId(SOURCE_LOGICAL_ID);
        alternateId.setAttributeString(CARD_NUMBER);
        objectReference.getAlternateId().add(alternateId);
        hasCards.setObjectReference(objectReference);
        relatedArrangement.getHasCards().add(hasCards);
        arrangementAssociation.setRelatedArrangement(relatedArrangement);
        return arrangementAssociation;
    }

    private Communication getSourceEvent() {
        Communication communication = new Communication();
        MaintenanceAuditData maintenanceAuditData = new MaintenanceAuditData();
        MaintenanceAuditElement maintenanceAuditElement = new MaintenanceAuditElement();
        maintenanceAuditElement.setMaintenanceAuditData(MAINTENANCE_AUDIT_DATA);
        maintenanceAuditElement.setMaintenanceAuditType(MAINTENANCE_AUDIT_TYPE);
        maintenanceAuditData.getHasMaintenanceAuditElement().add(maintenanceAuditElement);
        communication.setHasMaintenanceAuditData(maintenanceAuditData);
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName(CONDITION_NAME);
        ConditionType conditionType = new ConditionType();
        conditionType.setName(CONDITION_TYPE_NAME);
        ruleCondition.setHasConditionType(conditionType);
        ruleCondition.setResult(CONDITION_RESULT);
        communication.getHasObjectConditions().add(ruleCondition);
        return communication;
    }

}