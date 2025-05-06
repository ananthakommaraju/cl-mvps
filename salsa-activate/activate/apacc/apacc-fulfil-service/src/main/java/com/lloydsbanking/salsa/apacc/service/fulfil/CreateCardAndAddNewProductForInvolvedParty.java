package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.AddNewProductForInvolvedParty;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.CreateCreditCardAccountV1Retriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateCardAndAddNewProductForInvolvedParty {
    public static final int MAX_CARD_LENGTH = 19;
    public static final int CARD_LENGTH_TRIM = 3;
    private static final Logger LOGGER = Logger.getLogger(CreateCardAndAddNewProductForInvolvedParty.class);
    @Autowired
    CreateCreditCardAccountV1Retriever createCreditCardAccountV1Retriever;
    @Autowired
    AddNewProductForInvolvedParty addNewProductForInvolvedParty;
    @Autowired
    ValidateFulfilPendingCreditCardArrangement validateFulfilPendingCreditCardArrangement;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    public void cardCreationAndAddingNewProduct(RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails, FinanceServiceArrangement financeServiceArrangementResponse) {
        if (validateFulfilPendingCreditCardArrangement.isFulfillNewApplication(financeServiceArrangement.getApplicationType()) && validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(!applicationDetails.isApiFailureFlag(), financeServiceArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.CARD_CREATION_FAILURE)) {
            createCreditCardAccount(financeServiceArrangement, requestHeader, financeServiceArrangementResponse, applicationDetails);
            financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
            if (!applicationDetails.isApiFailureFlag()) {
                addNewProductForInvolvedParty.addNewProduct(requestHeader, financeServiceArrangement, applicationDetails);
            }
        }
    }

    private void createCreditCardAccount(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangementResponse, ApplicationDetails applicationDetails) {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(financeServiceArrangement, "Entering CreateCreditCard (f241V1)"));
        F241Resp f241Resp = createCreditCardAccountV1Retriever.createCreditCardAccount(financeServiceArrangement, requestHeader, applicationDetails);
        if (!applicationDetails.isApiFailureFlag()) {
            financeServiceArrangement.setAccountNumber(f241Resp.getAccountNumberExternalId());
            financeServiceArrangement.getPrimaryInvolvedParty().setPartyIdentifier(f241Resp.getCustomerNumberExternalId());
            financeServiceArrangementResponse.setAccountNumber(f241Resp.getAccountNumberExternalId());
            getCardData(financeServiceArrangement, financeServiceArrangementResponse, f241Resp);
            LOGGER.info("Exiting CreateCreditCard (f241V1) with CreditCardAccNo " + f241Resp.getAccountNumberExternalId());
        }
    }

    private void getCardData(FinanceServiceArrangement financeServiceArrangement, FinanceServiceArrangement financeServiceArrangementResponse, F241Resp f241Resp) {
        if (!f241Resp.getCardData().isEmpty() && f241Resp.getCardData().get(0).getCardNo() != null) {
            String f241respCardNo = f241Resp.getCardData().get(0).getCardNo();
            String cardNumber = (f241respCardNo.length() == MAX_CARD_LENGTH ? f241respCardNo.substring(CARD_LENGTH_TRIM) : f241respCardNo);
            financeServiceArrangement.setCreditCardNumber(cardNumber);
            financeServiceArrangementResponse.setCreditCardNumber(cardNumber);
        }
    }

}
