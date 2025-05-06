package com.lloydsbanking.salsa.apapca.service.utility;


public enum BranchFunctions {
    NOT_APPLICABLE(0),
    ACCOUNTING(1),
    MANAGER_IN_CHARGE_SELF_ACCOUNT(2),
    NOT_ACCOUNTING(3),
    ADMIN_PRIVATE_BANK_CUSTOMER(4),
    TELESERVICE(5),
    GLOBAL_PROCESSING_AND_AUTH(6),
    NON_ACCOUNTING_SORT_CODE(7),
    NOT_ORDINARILY_RESIDENT(8),
    CUSTOMER_CONTACT(9),
    LENDING_DISCRETION(10),
    NON_CUSTOMER_FACING(11),
    CUSTOMER_FACING(12),
    GLOBAL_ACCESS_INDICATOR(13),
    SECONDARY_CUSTOMER_CONTACT(14),
    PRIMARY_CUSTOMER_BRANCH(15),
    GENERAL_ENQUIRIES(16),
    WEALTH_AFFLUENT(17),
    WEALTH_MASS_AFFLUENT(18),
    WEALTH_RELATIONSHIP_MANAGER(19),
    BOS_LTB_CO_SERVICING_RRB(20),
    DEFAULT(21);


    private int branchFunctionCode;

    BranchFunctions(int branchFunctionCode) {
        this.branchFunctionCode = branchFunctionCode;
    }

    public int getBranchFunctionCode() {
        return this.branchFunctionCode;
    }

}
