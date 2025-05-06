package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import lib_sim_bo.businessobjects.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.springframework.stereotype.Component;

@Component
public class B765ToDepositArrangementResponse {
    public void createDepositArrangementResponse(StB765BAccCreateAccount response, DepositArrangement depositArrangement, ActivateProductArrangementResponse activateResponse) {
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(depositArrangement.getArrangementType())) {
            if (depositArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
                depositArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
            }
            depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(response.getStacc().getSortcode());
            depositArrangement.setAccountNumber(response.getStacc().getAccno());
        } else {
            activateResponse.getProductArrangement().getPrimaryInvolvedParty().setCustomerNumber(response.getCustnum());
        }
        activateResponse.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(response.getStacc().getSortcode());
        activateResponse.getProductArrangement().setAccountNumber(response.getStacc().getAccno());
        ExtraConditions extraConditions = new ExtraConditions();
        Condition condition = new Condition();
        condition.setReasonCode(String.valueOf(response.getSterror().getErrorno()));
        condition.setReasonText(response.getSterror().getErrormsg());
        extraConditions.getConditions().add(condition);
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setExtraConditions(extraConditions);
        depositArrangement.getPrimaryInvolvedParty().setCustomerNumber(response.getCustnum());
        activateResponse.setResultCondition(resultCondition);
        activateResponse.getProductArrangement().getPrimaryInvolvedParty().setCbsCustomerNumber(response.getCustnum());
    }
}
