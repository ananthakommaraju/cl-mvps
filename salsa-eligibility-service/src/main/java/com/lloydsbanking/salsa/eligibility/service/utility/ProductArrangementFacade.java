package com.lloydsbanking.salsa.eligibility.service.utility; // NOSONAR

import lb_gbo_sales.Customer;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
import lb_gbo_sales.businessobjects.CreditCardStatus;
import lb_gbo_sales.businessobjects.OvrdrftDtls;
import lib_sim_bo.businessobjects.ISABalance;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.OrganisationUnit;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ProductArrangementFacade { // NOSONAR

    lb_gbo_sales.ProductArrangement salesProductArrangement;

    lib_sim_bo.businessobjects.ProductArrangement serviceProductArrangement;


    boolean sales = true;

    private static final BigDecimal ISA_NOT_DEPOSITED_AMOUNT = new BigDecimal(0);

    private static final int ISA_FUNDED_COMPARATOR = 0;

    public ProductArrangementFacade(Object arrangement) {
        if (arrangement instanceof lb_gbo_sales.ProductArrangement) {
            salesProductArrangement = (lb_gbo_sales.ProductArrangement) arrangement;
            sales = true;
        }
        else {
            serviceProductArrangement = (lib_sim_bo.businessobjects.ProductArrangement) arrangement;
            sales = false;
        }
    }

    public String getLifecycleStatus() {
        if (sales) {
            return null != salesProductArrangement.getLifecycleStatus() ? salesProductArrangement.getLifecycleStatus().value() : null;
        }
        else {
            return null != serviceProductArrangement.getLifecycleStatus() ? serviceProductArrangement.getLifecycleStatus() : null;
        }
    }

    public List<String> getRelatedEvents() {
        if (sales) {
            return salesProductArrangement.getRelatedEvents();
        }
        else {
            return serviceProductArrangement.getRelatedEvents();
        }
    }

    public boolean isDepositArrangement() {
        if (sales) {
            return salesProductArrangement instanceof lb_gbo_sales.DepositArrangement;
        }
        else {
            return serviceProductArrangement instanceof lib_sim_bo.businessobjects.DepositArrangement;
        }
    }

    public boolean isSalesDepositArrangement() {
        if (sales) {
            return salesProductArrangement instanceof lb_gbo_sales.DepositArrangement;
        }
        else {
            return false;
        }
    }

    public String getSortCode() {
        if (sales) {
            return salesProductArrangement.getSortCode();
        }
        else {
            OrganisationUnit organisationUnit = serviceProductArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0);
            return organisationUnit.getSortCode();
        }
    }

    public String getInstructionMnemonic() {
        if (sales) {
            return salesProductArrangement.getInstructionMnemonic();
        }
        else {
            if (null != serviceProductArrangement.getAssociatedProduct() && null != serviceProductArrangement.getAssociatedProduct().getInstructionDetails()) {
                return serviceProductArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic();
            }
            else {
                return null;
            }
        }
    }

    public boolean isCreditCardFinanceServiceArrangement() {
        if (sales) {
            return salesProductArrangement instanceof CreditCardFinanceServiceArrangement;
        }
        else {
            return false;
        }
    }

    public boolean isHasEmbeddedInsurance() {
        if (sales) {
            if (null != salesProductArrangement.isHasEmbeddedInsurance()) {
                return salesProductArrangement.isHasEmbeddedInsurance();
            }
        }
        return false;
    }

    public Product getAssociatedProduct() {
        if (sales) {
            return null;
        }
        else {
            return serviceProductArrangement.getAssociatedProduct();
        }
    }

    public XMLGregorianCalendar getArrangementStartDate() {
        if (sales) {
            return salesProductArrangement.getStartDate();
        }
        else {
            return serviceProductArrangement.getArrangementStartDate();
        }
    }

    public String getParentInstructionMnemonic() {
        if (sales) {
            return salesProductArrangement.getParentInstructionMnemonic();
        }
        else {
            if (serviceProductArrangement.getAssociatedProduct() != null && serviceProductArrangement.getAssociatedProduct().getInstructionDetails() != null) {
                return serviceProductArrangement.getAssociatedProduct().getInstructionDetails().getParentInstructionMnemonic();
            }
            else {
                return null;
            }
        }
    }

    public boolean isISAFunded() {
        if (serviceProductArrangement instanceof lib_sim_bo.businessobjects.DepositArrangement) {
            if (isDepositedCurrentYear((lib_sim_bo.businessobjects.DepositArrangement) serviceProductArrangement)) {
                return true;
            }
        }
        else if (salesProductArrangement instanceof DepositArrangement) {
            DepositArrangement depositArrangement = (DepositArrangement) salesProductArrangement;
            if (depositArrangement.getMaximumTransactionAmount() != null && depositArrangement.getHeadRoomAmount() != null) {
                if (null != depositArrangement.getMaximumTransactionAmount().getValue() && !depositArrangement.getMaximumTransactionAmount().getValue().equals(depositArrangement.getHeadRoomAmount().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isISAFunded(ISABalance isaBalance) {
        lib_sim_bo.businessobjects.DepositArrangement depositArrangement = new lib_sim_bo.businessobjects.DepositArrangement();
        depositArrangement.setISABalance(isaBalance);
        return isDepositedCurrentYear(depositArrangement);
    }

    private boolean isDepositedCurrentYear(lib_sim_bo.businessobjects.DepositArrangement depositArrangement) {
        return (null != depositArrangement.getISABalance() &&
            null != depositArrangement.getISABalance().getMaximumLimitAmount() &&
            null != depositArrangement.getISABalance().getMaximumLimitAmount().getAmount() &&
            depositArrangement.getISABalance().getMaximumLimitAmount().getAmount().compareTo(ISA_NOT_DEPOSITED_AMOUNT) > ISA_FUNDED_COMPARATOR);
    }

    public CreditCardStatus getCardStatus() {
        if (isCreditCardFinanceServiceArrangement()) {
            CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = (CreditCardFinanceServiceArrangement) salesProductArrangement;
            return creditCardFinanceServiceArrangement.getCardStatus();
        }
        return null;
    }

    public String getArrangementType() {
        if (sales) {
            return salesProductArrangement.getArrangementType();
        }
        return null;
    }

    public OvrdrftDtls getOvrdrftDtls() {
        if (sales) {
            return salesProductArrangement.getOvrdrftDtls();
        }
        return null;
    }

    public XMLGregorianCalendar getStartDate() {
        if (sales) {
            return salesProductArrangement.getStartDate();
        }
        return serviceProductArrangement.getArrangementStartDate();

    }

    public List<Customer> getParticipantCusomters() {
        if (sales) {
            return salesProductArrangement.getParticipantCusomters();
        }
        return Collections.emptyList();
    }

    public boolean isCapAccountRestricted() {
        if (sales) {
            if (null != salesProductArrangement.isCapAccountRestricted()) {
                return salesProductArrangement.isCapAccountRestricted();
            }
        }
        return false;
    }

    public String getAccountNumber() {
        if (sales) {
            return salesProductArrangement.getAccountNumber();
        }
        return serviceProductArrangement.getAccountNumber();
    }

    public List<ProductArrangementIndicator> getCbsIndicators() {
        if (sales) {
            return Collections.emptyList();
        }
        return serviceProductArrangement.getCbsIndicators();
    }


    public lib_sim_bo.businessobjects.Organisation getFinancialInstitution() {

        if (sales) {
            return null;
        }
        else {
            return serviceProductArrangement.getFinancialInstitution();
        }
    }

    public boolean isServicingProductArrangement() {
        return sales ? false : true;
    }

    public InstructionDetails getInstructionDetails() {
        if (null != this &&
            null != getAssociatedProduct() &&
            null != getAssociatedProduct().getInstructionDetails()) {
            return getAssociatedProduct().getInstructionDetails();
        }
        return null;
    }

    public ISABalance getISABalance() {
        if (isServicingProductArrangement()) {
            lib_sim_bo.businessobjects.DepositArrangement depositArrangement = (lib_sim_bo.businessobjects.DepositArrangement) serviceProductArrangement;
            return depositArrangement.getISABalance();
        }
        return null;
    }
}
