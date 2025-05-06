package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;
import lib_sim_bo.businessobjects.ProductOptions;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class QuestionIsCBSIndicatorPresentOnAllAccounts extends AbstractProductListAccountQuestion implements AskQuestion {
    private static final String EXT_SYS_ID_CURRENT_ACCOUNT = "00004";

    protected boolean isEitherCurrentOrSavingsAccount;

    protected CheckBalanceRetriever checkBalanceRetriever;

    private static final boolean IS_WZ_REQUEST = true;

    public static QuestionIsCBSIndicatorPresentOnAllAccounts pose() {
        return new QuestionIsCBSIndicatorPresentOnAllAccounts();
    }

    public boolean ask() throws EligibilityException {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            List<ProductOptions> productOptionsList = new ArrayList<>();
            Product associatedProduct = customerArrangement.getAssociatedProduct();
            boolean isCAApplicationOrCBSAccount;
            if (isEitherCurrentOrSavingsAccount && null != associatedProduct) {

                isCAApplicationOrCBSAccount = isCAApplicationOrCBSAccount(associatedProduct);
                InstructionDetails instructionDetails = customerArrangement.getInstructionDetails();
                if (isISAProductOrCAApplicationAndCBSProduct(instructionDetails, isCAApplicationOrCBSAccount)) {
                    productOptionsList = getProductOptions(customerArrangement, isCAApplicationOrCBSAccount);
                }
            }
            if (!isCbsIndicatorNotPresent(threshold, productOptionsList)) {
                return true;
            }
        }
        return false;
    }

    private List<ProductOptions> getProductOptions(ProductArrangementFacade customerArrangement, boolean isCAApplicationOrCBSAccount) throws EligibilityException {
        List<ProductOptions> productOptionsList;
        String sortCode = customerArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        String accNo = customerArrangement.getAccountNumber();
        String cbsAppGrp = appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, sortCode, IS_WZ_REQUEST);
        List<ProductArrangementIndicator> indicatorList;
        try {
            indicatorList = checkBalanceRetriever.getCBSIndicators(requestHeader, sortCode, accNo, cbsAppGrp);
        }
        catch (SalsaInternalResourceNotAvailableException | SalsaExternalServiceException e) {
            throw new EligibilityException(e);
        }

        productOptionsList = updateProductOptions(isCAApplicationOrCBSAccount, indicatorList);
        return productOptionsList;
    }

    private boolean isISAProductOrCAApplicationAndCBSProduct(InstructionDetails instructionDetails, boolean isCAApplicationOrCBSAccount) {
        if (isCAApplicationOrCBSAccount) {
            return true;
        }
        else {
            if (null != instructionDetails && null != instructionDetails.getParentInstructionMnemonic()) {
                if (Mnemonics.GROUP_ISA.equalsIgnoreCase(instructionDetails.getParentInstructionMnemonic())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCbsIndicatorNotPresent(final String signatureCheckIndicator, final List<ProductOptions> productOptionsList) {
        if (!StringUtils.isEmpty(signatureCheckIndicator)) {
            String[] indicators = signatureCheckIndicator.split(":");
            for (String indicator : indicators) {
                {
                    if (!CollectionUtils.isEmpty(productOptionsList)) {
                        for (ProductOptions productOption : productOptionsList) {
                            if (null != productOption && indicator.equals(productOption.getOptionsValue())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isCAApplicationOrCBSAccount(final Product associatedProduct) throws EligibilityException {
        if (!CollectionUtils.isEmpty(associatedProduct.getExternalSystemProductIdentifier())) {
            ExtSysProdIdentifier extSysProdIdentifier = associatedProduct.getExternalSystemProductIdentifier().get(0);
            if (null != extSysProdIdentifier.getSystemCode() && EXT_SYS_ID_CURRENT_ACCOUNT.equalsIgnoreCase(extSysProdIdentifier.getSystemCode())) {
                return true;
            }
        }
        return false;
    }

    private List<ProductOptions> updateProductOptions(boolean isCAApplicationOrCBSAccount, List<ProductArrangementIndicator> indicatorList) {
        List<ProductOptions> productOptionsList = new ArrayList<>();
        if (isCAApplicationOrCBSAccount && !indicatorList.isEmpty()) {
            for (ProductArrangementIndicator indicator : indicatorList) {
                ProductOptions productOption = new ProductOptions();
                productOption.setOptionsValue(indicator.getCode().toString());
                productOption.setOptionsDescription(indicator.getText());
                productOptionsList.add(productOption);
            }
        }
        return productOptionsList;
    }

    public QuestionIsCBSIndicatorPresentOnAllAccounts givenEitherCurrentOrSavingsAccount(boolean isEitherCurrentOrSavingsAccount) {
        this.isEitherCurrentOrSavingsAccount = isEitherCurrentOrSavingsAccount;
        return this;
    }

    public QuestionIsCBSIndicatorPresentOnAllAccounts givenCheckBalanceRetrieverClientInstance(CheckBalanceRetriever checkBalanceRetriever) {
        this.checkBalanceRetriever = checkBalanceRetriever;
        return this;
    }
}
