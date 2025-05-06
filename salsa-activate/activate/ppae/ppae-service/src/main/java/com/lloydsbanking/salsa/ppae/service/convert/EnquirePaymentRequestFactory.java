package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.soap.fs.ftp.EnquirePaymentInstructionFacilityDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementAssociation;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductAccessArrangement;
import com.lloydstsb.schema.enterprise.lcsm_common.AlternateId;
import com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference;
import com.lloydstsb.schema.enterprise.lcsm_communication.TransferInstruction;
import com.lloydstsb.schema.enterprise.lcsm_event.EventType;
import com.lloydstsb.schema.enterprise.lcsm_resourceitem.FinancialTransactionCard;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class EnquirePaymentRequestFactory {
    private static final Logger LOGGER = Logger.getLogger(EnquirePaymentRequestFactory.class);
    private static final String ALTERNATE_ID = "CREDIT_CARD_NUMBER";
    private static final String EVENT_TYPE = "CHECK_FASTER_PAYMENT_AVAILABILITY";

    public EnquirePaymentInstructionFacilityDetailsRequest convert(String creditCardNumber) {
        EnquirePaymentInstructionFacilityDetailsRequest request = new EnquirePaymentInstructionFacilityDetailsRequest();
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString(ALTERNATE_ID);
        alternateId.setValue(creditCardNumber);
        ObjectReference objectReference = new ObjectReference();
        objectReference.getAlternateId().add(alternateId);
        FinancialTransactionCard financialTransactionCard = new FinancialTransactionCard();
        financialTransactionCard.setObjectReference(objectReference);
        ProductAccessArrangement productAccessArrangement = new ProductAccessArrangement();
        productAccessArrangement.getHasCards().add(financialTransactionCard);
        ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
        arrangementAssociation.setRelatedArrangement(productAccessArrangement);
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.getArrangementAssociations().add(arrangementAssociation);
        TransferInstruction transferInstruction = new TransferInstruction();
        transferInstruction.setSourceArrangement(financeServiceArrangement);
        EventType eventType = new EventType();
        eventType.setValue(EVENT_TYPE);
        transferInstruction.setHasEventType(eventType);
        request.setPaymentInstructionIdentifier(transferInstruction);
        return request;
    }
}
