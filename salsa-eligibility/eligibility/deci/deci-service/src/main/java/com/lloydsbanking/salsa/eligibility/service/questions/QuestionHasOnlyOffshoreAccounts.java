package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.StringTokenizer;

public class QuestionHasOnlyOffshoreAccounts extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionHasOnlyOffshoreAccounts pose() {
        return new QuestionHasOnlyOffshoreAccounts();
    }

    public boolean ask() {
        for (ProductArrangementFacade arrangement : productArrangements) {
            if (arrangement.isSalesDepositArrangement()) {
                return (!isSortCodePresent(arrangement, threshold));

            }
            else if (arrangement.isServicingProductArrangement()) {
                if (null != arrangement.getFinancialInstitution()
                    && !CollectionUtils.isEmpty(arrangement.getFinancialInstitution().getHasOrganisationUnits())
                    && null != arrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode()) {
                    return (!isSortCodePresent(arrangement, threshold));
                }
            }
        }
        return false;
    }

    private boolean isSortCodePresent(ProductArrangementFacade arrangement, String threshold) {
        String sortCode = arrangement.getSortCode();
        StringTokenizer sortCodes = new StringTokenizer(threshold, ":");
        while (sortCodes.hasMoreTokens()) {
            if (sortCodes.nextToken().equals(sortCode)) {
                return true;
            }
        }
        return false;
    }
}
