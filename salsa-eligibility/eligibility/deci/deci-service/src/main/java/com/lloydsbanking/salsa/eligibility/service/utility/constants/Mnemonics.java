package com.lloydsbanking.salsa.eligibility.service.utility.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Mnemonics {
    String CASH_ISA_SAVER = "P_CISA_SAV";

    String CASH_ISA = "P_CSH_ISA";
    String ISA = "ISA";

    String GROUP_ISA = "G_ISA";

    String WEB_SAVER_FIXED = "G_ONL_FRTD";

    String FIXED_SAVER = "G_FR_TD";

    String LIFE_INSURANCE = "G_INSURANCE";

    Set<String> CLUB_ACCOUNTS = new HashSet(Arrays.asList("P_CLUB", "P_SLVR_CLB", "P_GOLD_CLB", "P_PLAT_CLB", "P_PREM_CLB", "P_CLB_MF", "P_CLB_PREM", "P_CLB_PB"));

    Set<String> FIXED_ISAS = new HashSet(Arrays.asList("P_ISA_F_1Y", "P_ISA_F_2Y", "P_ISA_F_3Y", "P_ISA_F_4Y", "P_ISA_F_5Y"));

    Set<String> AVA_ACCOUNTS = new HashSet(Arrays.asList("P_GOLD", "P_PLAT", "P_PREM", "P_SILVER", "P_GOLDVTG", "P_PLATVTG", "P_PREMVTG", "P_SLVRVTG"));


    Set<String> CURRENT_ACCOUNTS = new HashSet(Arrays.asList("G_PCA", "G_AVA", "G_VANTAGE"));
    Set<String> VANTAGE_HOLDING = new HashSet(Arrays.asList("G_VANTAGE"));
}

