package com.lloydsbanking.salsa.opaloans.service.convert;

import com.lloydsbanking.salsa.opaloans.ProductOptionsCode;
import com.lloydsbanking.salsa.soap.fs.loan.StLoanProduct;
import com.lloydstsb.ib.wsbridge.loan.StB231BLoanPartyProductsGet;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOptions;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class B231ResponseToEligibleProductsConverter {
    private static final String OPTIONS_VALUE_IF_INSURANCE_AVAIL = "true";

    private static final String OPTIONS_VALUE_IF_INSURANCE_NOT_AVAIL = "false";

    private static final String ASSESSMENT_TYPE_CREDIT_SCORE = "CREDIT_SCORE";

    private static final String SCORE_RESULT_ACCEPT = "ACCEPT";

    public void convert(final StB231BLoanPartyProductsGet b231Response, FinanceServiceArrangement productArrangement) {
        if (!CollectionUtils.isEmpty(b231Response.getAstloanproduct())) {
            List<Product> eligibleProducts = new ArrayList<>();
            for (StLoanProduct stLoanProduct : b231Response.getAstloanproduct()) {
                Product product = new Product();
                product.setProductName(stLoanProduct.getLoanprodtxt());
                product.setProductIdentifier(String.valueOf(stLoanProduct.getLoanprodid()));
                product.getProductoptions().addAll(retrieveProductOptionsList(stLoanProduct));
                eligibleProducts.add(product);
            }
            productArrangement.getOfferedProducts().addAll(eligibleProducts);
        }

        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType(ASSESSMENT_TYPE_CREDIT_SCORE);
        customerScore.setScoreResult(SCORE_RESULT_ACCEPT);
        customerScore.setScoreIdentifier(b231Response.getCreditscoreno());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().clear();
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);

        if (null != b231Response.getStloanheader() && null != b231Response.getStloanheader().getCustnum()) {
            productArrangement.getPrimaryInvolvedParty().setCustomerNumber(b231Response.getStloanheader().getCustnum());
        }
    }

    private List<ProductOptions> retrieveProductOptionsList(final StLoanProduct stLoanProduct) {
        List<ProductOptions> productOptionsList = new ArrayList<>();
        productOptionsList.add(createProductOptions(ProductOptionsCode.CURRENCY_CODE.getValue(), stLoanProduct.getCurrencycode()));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MINIMUM_LOAN_AMOUNT.getValue(), String.valueOf(stLoanProduct.getAmtMinLoan())));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MAXIMUM_LOAN_AMOUNT.getValue(), String.valueOf(stLoanProduct.getAmtMaxLoan())));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MINIMUM_LOAN_TERM.getValue(), String.valueOf(stLoanProduct.getLoantermMin())));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MAXIMUM_LOAN_TERM.getValue(), String.valueOf(stLoanProduct.getLoantermMax())));

        if ("Y".equals(stLoanProduct.getInsuranceavail())) {
            productOptionsList.add(createProductOptions(ProductOptionsCode.INSURANCE_AVAILABLE_INDICATOR.getValue(), OPTIONS_VALUE_IF_INSURANCE_AVAIL));
        } else {
            productOptionsList.add(createProductOptions(ProductOptionsCode.INSURANCE_AVAILABLE_INDICATOR.getValue(), OPTIONS_VALUE_IF_INSURANCE_NOT_AVAIL));
        }

        if (null != stLoanProduct.getStloancharges()) {
            productOptionsList.add(createProductOptions(ProductOptionsCode.LETTER_CHARGES.getValue(), String.valueOf(stLoanProduct.getStloancharges().getAmtLetterCharge())));
            productOptionsList.add(createProductOptions(ProductOptionsCode.DAYS_INTEREST_CHARGED.getValue(), String.valueOf(stLoanProduct.getStloancharges().getNDaysIntCharge())));
            productOptionsList.add(createProductOptions(ProductOptionsCode.MAXIMUM_CHARGE_AMOUNT.getValue(), String.valueOf(stLoanProduct.getStloancharges().getAmtMaxCharge())));
            productOptionsList.add(createProductOptions(ProductOptionsCode.ADMIN_CHARGES.getValue(), String.valueOf(stLoanProduct.getStloancharges().getAmtAdminCharge())));
            productOptionsList.add(createProductOptions(ProductOptionsCode.LOAN_TERM_EXEMPTION_START_DATE.getValue(), String.valueOf(stLoanProduct.getStloancharges().getLoantermExemptStart())));
            productOptionsList.add(createProductOptions(ProductOptionsCode.LOAN_TERM_EXEMPTION_END_DATE.getValue(), String.valueOf(stLoanProduct.getStloancharges().getLoantermExemptEnd())));
        }

        productOptionsList.add(createProductOptions(ProductOptionsCode.MINIMUM_LOAN_DEFER_TERM.getValue(), String.valueOf(stLoanProduct.getLoantermMinDefer())));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MAXIMUM_LOAN_DEFER_TERM.getValue(), String.valueOf(stLoanProduct.getLoantermMaxDefer())));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS.getValue(), String.valueOf(stLoanProduct.getLoantermMinRepaymentHol())));
        productOptionsList.add(createProductOptions(ProductOptionsCode.MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS.getValue(), String.valueOf(stLoanProduct.getLoantermMaxRepaymentHol())));

        if (!StringUtils.isEmpty(stLoanProduct.getUrltxtDisplay())) {
            productOptionsList.add(createProductOptions(ProductOptionsCode.URL_TEXT_DISPLAY.getValue(), stLoanProduct.getUrltxtDisplay()));
        }

        if (!StringUtils.isEmpty(stLoanProduct.getUrltxtURL())) {
            productOptionsList.add(createProductOptions(ProductOptionsCode.URL.getValue(), stLoanProduct.getUrltxtURL()));
        }
        return productOptionsList;
    }

    private ProductOptions createProductOptions(final String optionsCode, final String optionsValue) {
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode(optionsCode);
        productOptions.setOptionsValue(optionsValue);
        return productOptions;
    }
}
