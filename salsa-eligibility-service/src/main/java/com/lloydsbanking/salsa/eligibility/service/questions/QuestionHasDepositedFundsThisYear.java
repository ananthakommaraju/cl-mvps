package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.ISADetailsSubGp;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.ISABalance;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class QuestionHasDepositedFundsThisYear extends AbstractProductListQuestion implements AskQuestion {
    private static final Logger LOGGER = Logger.getLogger(QuestionHasDepositedFundsThisYear.class);

    private static final String EXT_SYS_ID_CURRENT_ACCOUNT = "00004";

    protected boolean isEitherCurrentOrSavingsAccount;

    protected CheckBalanceRetriever checkBalanceRetriever;

    private boolean isWzRequest = false;

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    public static QuestionHasDepositedFundsThisYear pose() {
        return new QuestionHasDepositedFundsThisYear();
    }

    public boolean ask() throws EligibilityException {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                ISABalance isaBalance;
                if (isGroupISA(productArrangement)) {
                    if (isWzRequest) {
                        isaBalance = getIsaBalance(productArrangement);
                        if (productArrangement.isISAFunded(isaBalance)) {
                            return true;
                        }
                    }
                    else if (productArrangement.isDepositArrangement() && productArrangement.isISAFunded()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ISABalance getIsaBalance(final ProductArrangementFacade productArrangement) throws EligibilityException {
        Product associatedProduct = productArrangement.getAssociatedProduct();
        boolean isCAApplicationOrCBSAccount;
        if (isEitherCurrentOrSavingsAccount && null != associatedProduct) {
            isCAApplicationOrCBSAccount = isCAApplicationOrCBSAccount(associatedProduct);
            InstructionDetails instructionDetails = productArrangement.getInstructionDetails();
            if (isISAProductOrCAApplicationAndCBSProduct(instructionDetails, isCAApplicationOrCBSAccount)) {
                String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
                String accNo = productArrangement.getAccountNumber();
                String cbsAppGrp = appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, sortCode, isWzRequest);
                E141Resp checkBalanceResponse;
                try {
                    checkBalanceResponse = checkBalanceRetriever.getCheckBalance(requestHeader, sortCode, accNo, cbsAppGrp);
                }
                catch (SalsaInternalResourceNotAvailableException | SalsaExternalServiceException e) {
                    throw new EligibilityException(e);
                }
                if (null != checkBalanceResponse && null != checkBalanceResponse.getISADetailsGp()) {
                    return convertToIsaBalance(checkBalanceResponse.getISADetailsGp().getISADetailsSubGp());
                }
            }
        }
        return null;
    }

    private boolean isGroupISA(ProductArrangementFacade productArrangement) {
        return productArrangement.getParentInstructionMnemonic() != null && Mnemonics.GROUP_ISA.equalsIgnoreCase(productArrangement.getParentInstructionMnemonic());
    }

    private boolean isISAProductOrCAApplicationAndCBSProduct(InstructionDetails instructionDetails, boolean isCAApplicationOrCBSAccount) {
        if (isCAApplicationOrCBSAccount) {
            return true;
        }
        else {
            if (null != instructionDetails && !StringUtils.isEmpty(instructionDetails.getParentInstructionMnemonic())) {
                if (Mnemonics.GROUP_ISA.equalsIgnoreCase(instructionDetails.getParentInstructionMnemonic())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCAApplicationOrCBSAccount(final Product associatedProduct) {
        if (!CollectionUtils.isEmpty(associatedProduct.getExternalSystemProductIdentifier())) {
            ExtSysProdIdentifier extSysProdIdentifier = associatedProduct.getExternalSystemProductIdentifier().get(0);
            if (null != extSysProdIdentifier.getSystemCode() && EXT_SYS_ID_CURRENT_ACCOUNT.equalsIgnoreCase(extSysProdIdentifier.getSystemCode())) {
                return true;
            }
        }
        return false;
    }

    private ISABalance convertToIsaBalance(ISADetailsSubGp isaDetailsSubGp) {
        ISABalance isaBalance = new ISABalance();
        CurrencyAmount headRoomAmount = new CurrencyAmount();
        CurrencyAmount maximumLimitAmount = new CurrencyAmount();
        if (null != isaDetailsSubGp) {
            if (!StringUtils.isEmpty(isaDetailsSubGp.getISARmnDpsAm())) {
                BigDecimal amount = new BigDecimal(isaDetailsSubGp.getISARmnDpsAm()).divide(HUNDRED);
                headRoomAmount.setAmount(amount);
            }
            if (!StringUtils.isEmpty(isaDetailsSubGp.getTaxYearTotalDepositAm())) {
                BigDecimal amount = new BigDecimal(isaDetailsSubGp.getTaxYearTotalDepositAm()).divide(HUNDRED);
                maximumLimitAmount.setAmount(amount);
            }
        }
        isaBalance.setHeadRoomAmount(headRoomAmount);
        isaBalance.setMaximumLimitAmount(maximumLimitAmount);
        LOGGER.info("HeadRoomAmount: " + headRoomAmount + " maximumLimitAmt :" + maximumLimitAmount + " returned by E141");
        return isaBalance;
    }

    public QuestionHasDepositedFundsThisYear givenCheckBalanceRetrieverClientInstance(CheckBalanceRetriever checkBalanceRetriever) {
        this.checkBalanceRetriever = checkBalanceRetriever;
        return this;
    }

    public QuestionHasDepositedFundsThisYear givenEitherCurrentOrSavingsAccount(boolean isEitherCurrentOrSavingsAccount) {
        this.isEitherCurrentOrSavingsAccount = isEitherCurrentOrSavingsAccount;
        return this;
    }

    public QuestionHasDepositedFundsThisYear givenIsWzRequest(boolean isWzRequest) {
        this.isWzRequest = isWzRequest;
        return this;
    }
}
