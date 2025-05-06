package com.lloydsbanking.salsa.apapca.service.fulfil.converter;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.apapca.service.fulfil.rules.RateType;
import com.lloydsbanking.salsa.downstream.account.client.b276.StODRatesBuilder;
import com.lloydsbanking.salsa.soap.fs.account.StODRates;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class StODRatesFactory {
    private static final String FACTOR_TEN_THOUSAND = "10000";
    private static final int ROUND_OFF_DECIMAL_PLACES_FOUR = 4;
    private static final String DEFAULT_RATE_VALUE = "0000000";

    public StODRates getStODRates(Boolean isBBaseRateLinked, XMLGregorianCalendar expiryDate, Map<String, BigDecimal> interestRateMap) {
        StODRatesBuilder stODRatesBuilder = new StODRatesBuilder();
        BigDecimal freeOverdraft = getDefaultValueIfNull(interestRateMap.get(RateType.INT_FREE_OVERDRAFT));
        BigDecimal excessFee = getDefaultValueIfNull(interestRateMap.get(RateType.AMT_EXCESS_FEE));
        BigDecimal excessFeeBalIncr = getDefaultValueIfNull(interestRateMap.get(RateType.AMT_EXCESS_FEE_BAL_INC));
        BigDecimal feeOverdraft = getDefaultValueIfNull(interestRateMap.get(RateType.AMT_MONTHLY_FEE));
        BigDecimal totalCreditCost = getDefaultValueIfNull(interestRateMap.get(RateType.TOTAL_COST_OF_CREDIT));
        String base = formatRateAs7DigitInteger(interestRateMap.get(RateType.BASE_INT_RATE));
        String marginOBR = formatRateAs7DigitInteger(interestRateMap.get(RateType.MARGIN_OBR_RATE));
        BigInteger excessFeeCap = getDefaultValueIfNull(interestRateMap.get(RateType.EXCESS_FEE_CAP)).toBigInteger();
        String authEAR = formatRateAs7DigitInteger(interestRateMap.get(RateType.AUTH_EAR));
        String authMonthly = formatRateAs7DigitInteger(interestRateMap.get(RateType.AUTH_MONTHLY));
        String unauthEAR = formatRateAs7DigitInteger(interestRateMap.get(RateType.UNAUTH_EAR));
        String unauthMonthly = formatRateAs7DigitInteger(interestRateMap.get(RateType.UNAUTH_MONTHLY_AMT_MONTHLY_FEE));
        return stODRatesBuilder.amountDetails(ActivateCommonConstant.ApaPcaServiceConstants.CURRENCY_CODE_GBP, freeOverdraft, excessFee, excessFeeBalIncr, feeOverdraft, totalCreditCost, BigDecimal.ZERO)
                .baseIntRateAndRateMargin(base, marginOBR)
                .baseRateLinked(isBBaseRateLinked).dateExpired(expiryDate)
                .excessFeeCap(excessFeeCap).intRateAuthDetails(authEAR, authMonthly)
                .intRateUnauthDetails(unauthEAR, unauthMonthly).build();
    }

    private String formatRateAs7DigitInteger(BigDecimal rate) {
        return rate == null ? DEFAULT_RATE_VALUE :
                String.format("%07d", rate.movePointRight(4).setScale(0, RoundingMode.HALF_DOWN).toBigInteger());
    }

    private BigDecimal getDefaultValueIfNull(BigDecimal rate) {
        return rate == null ? BigDecimal.ZERO : rate;
    }
}
