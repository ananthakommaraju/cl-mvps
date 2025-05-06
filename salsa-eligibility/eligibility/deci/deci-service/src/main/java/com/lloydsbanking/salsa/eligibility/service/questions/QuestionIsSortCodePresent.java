package com.lloydsbanking.salsa.eligibility.service.questions;

import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;

public class QuestionIsSortCodePresent extends AbstractProductListQuestion implements AskQuestion {
    public static QuestionIsSortCodePresent pose() {
        return new QuestionIsSortCodePresent();
    }

    @Override
    public boolean ask() {
        return isHasOrganisationUnitsListEmpty()
            && null != productArrangements.get(0).getFinancialInstitution().getHasOrganisationUnits().get(0)
            && !StringUtils.isEmpty(productArrangements.get(0).getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode())
            && !StringUtils.isEmpty(productArrangements.get(0).getAccountNumber());

    }

    private boolean isHasOrganisationUnitsListEmpty() {
        return !CollectionUtils.isEmpty(productArrangements)
            && null != productArrangements.get(0)
            && null != productArrangements.get(0).getFinancialInstitution()
            && !CollectionUtils.isEmpty(productArrangements.get(0).getFinancialInstitution().getHasOrganisationUnits());
    }
}
