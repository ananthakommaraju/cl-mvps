package com.lloydsbanking.salsa.eligibility.service.utility.constants.eventtype;

public interface Payment {

    String VALIDATE_N_PAYMENT = "53";

    String VALIDATE_MAKE_PAYMENT = "54";

    String MAKE_NEW_PAYMENT = "55";

    String VALIDATE_PAYMNT_UPDATE = "57";

    String EARLY_PAYMENT_READ = "60";

    String P2P_REGISTER_TO_RECEIVE = "693";

    String P2P_SEND_PAYMENT = "694";

    String REGULAR_PAYMENT = "503";

    String MAKE_INTERNATIONAL_PAYMENT = "537";

    String MONITOR_INTERNATIONAL_PAYMENT = "538";

    String DELETE_INTERNATIONAL_BENEFICIARY = "539";

    String VALIDATE_PMT_LIMIT_AMT = "583";

    String VALIDATE_PAYMENT_MAKE = "596";

    String VALIDATE_MULTIPLE_PYMTS = "598";

}
