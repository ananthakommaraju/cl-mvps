package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.loan.client.b232.St2LoanIllustrationBuilder;
import com.lloydsbanking.salsa.downstream.loan.client.b232.StLoanCoreBuilder;
import com.lloydsbanking.salsa.ppae.service.rule.EmploymentStatus;
import com.lloydsbanking.salsa.soap.fs.loan.St2LoanCoreDetails;
import com.lloydsbanking.salsa.soap.fs.loan.St2LoanEarly;
import com.lloydsbanking.salsa.soap.fs.loan.St2LoanIllustration;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydsbanking.salsa.soap.pad.f263.objects.IllustrationDetails;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class StLoanCoreFactory {
    private static final Logger LOGGER = Logger.getLogger(StLoanCoreFactory.class);
    private static final int ROUND_OFF_DECIMAL_PLACES = 4;

    private static final String TEN_THOUSAND = "10000";

    private static final String DIVISOR_HUNDRED = "100";

    private static final BigDecimal DAILY_RATE_DIVISOR = new BigDecimal(365*100);

    private static final String GUARANTEED_PRODUCT_MAILING = "1";

    private static final int INSURANCE_NOT_REQUIRED = 0;

    private static final int INSURANCE_REQUIRED = 1;

    @Autowired
    EmploymentStatus employmentStatus;

    public St2LoanCoreDetails getStLoanDetails(F263Resp f263Resp) {
        StLoanCoreBuilder stLoanCoreBuilder = new StLoanCoreBuilder();
        BigInteger ocisId = null;
        if (f263Resp.getApplicantDetails().getParty().get(0).getPartyId() != null) {
            ocisId = BigInteger.valueOf(f263Resp.getApplicantDetails().getParty().get(0).getPartyId());
        }
        String partyIdPersId = f263Resp.getApplicantDetails().getParty().get(0).getPersId();
        String custNum = f263Resp.getApplicantDetails().getParty().get(0).getCSExtPartyIdTx();
        stLoanCoreBuilder.stLoanHeader(ocisId, partyIdPersId, custNum);
        String creditScoreNo = f263Resp.getBIdentifiers().getRequestNo();
        stLoanCoreBuilder.creditScoreNo(creditScoreNo);
        IllustrationDetails illustrationDetails = f263Resp.getIllustrationDetails();
        Integer loanProdid = illustrationDetails.getProduct().getProductId();
        Integer insuranceInd = (illustrationDetails.getProduct().getInsuranceTakeUpIn().equals("1") ? INSURANCE_REQUIRED : INSURANCE_NOT_REQUIRED);
        String campaignCode = f263Resp.getApplicationDetails().getCampaignCodeTx();
        String currencyCode = illustrationDetails.getProduct().getCurrencyCd();
        stLoanCoreBuilder.stLoandetails1(loanProdid, insuranceInd, campaignCode, currencyCode, null, null, null, null, null);
        stLoanCoreBuilder.stLoanIllustrationValues(getSt2LoanIllustration(f263Resp));
        DateFactory dateFactory = new DateFactory();
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("ddMMyyyyHHmmss");
        XMLGregorianCalendar tmStmpLastUp = dateFactory.stringToXMLGregorianCalendar(f263Resp.getBIdentifiers().getUpdateTs(), fastDateFormat);
        stLoanCoreBuilder.tmStmpLastPadUpdateAndLoanPurpose(tmStmpLastUp, f263Resp.getApplicationDetails().getLoanPurposeCd());
        String accPaySortCode = f263Resp.getApplicationDetails().getPaymentSortCd();
        String accPayAcctNo = f263Resp.getApplicationDetails().getPaymentAccNo();
        stLoanCoreBuilder.stAccPayment(null, null, accPaySortCode, accPayAcctNo, null, null);
        String accRePaySortCode = f263Resp.getApplicationDetails().getRepaymentSortCd();
        String accRePayAcctNo = f263Resp.getApplicationDetails().getRepaymentAccNo();
        stLoanCoreBuilder.stAccRepayment(null, null, accRePaySortCode, accRePayAcctNo, null, null);

        String employmtstatuscd = null;
        if (f263Resp.getApplicantDetails().getParty().get(0).getEmploymentStatusCd() != null) {
            employmtstatuscd = employmentStatus.getEmploymentStatusCode(f263Resp.getApplicantDetails().getParty().get(0).getEmploymentStatusCd());
        }
        stLoanCoreBuilder.installPayDayNoAndEmpStatusCd(f263Resp.getApplicationDetails().getRepaymentDayOfMonthNo(), employmtstatuscd);
        stLoanCoreBuilder.guaranteedProductMailing(GUARANTEED_PRODUCT_MAILING);
        BigDecimal amtLetterCharge = convertStringToBigDecimal(f263Resp.getIllustrationDetails().getCCAVariables().getLetterChargeAm(), DIVISOR_HUNDRED);
        Integer daysIntCharge = null;
        if (f263Resp.getIllustrationDetails().getCCAVariables().getEarlySettParameters().getDaysIntChgQy() != null) {
            daysIntCharge = Integer.valueOf(f263Resp.getIllustrationDetails().getCCAVariables().getEarlySettParameters().getDaysIntChgQy());
        }
        BigDecimal amtMaxCharge = convertStringToBigDecimal(f263Resp.getIllustrationDetails().getCCAVariables().getEarlySettParameters().getMaxChgAm(), DIVISOR_HUNDRED);
        BigDecimal amtAdminCharge = convertStringToBigDecimal(f263Resp.getIllustrationDetails().getCCAVariables().getEarlySettParameters().getAdministrationCg(), DIVISOR_HUNDRED);
        BigInteger loanTermExemptSt = BigInteger.valueOf(f263Resp.getIllustrationDetails().getCCAVariables().getEarlySettParameters().getStartTermExempQy());
        BigInteger loanTermExemptEnd = BigInteger.valueOf(f263Resp.getIllustrationDetails().getCCAVariables().getEarlySettParameters().getEndTermExempQy());
        BigDecimal primaryPeriodAnnualRate = convertStringToBigDecimal(f263Resp.getIllustrationDetails().getInterestRates().getPriAnnualIntPcRt(), TEN_THOUSAND);
        BigDecimal amtBorrowed = convertStringToBigDecimal(f263Resp.getIllustrationDetails().getLoanAm().getLoanCshAm(), DIVISOR_HUNDRED);
        BigDecimal amtDailyIntVal = (primaryPeriodAnnualRate.multiply(amtBorrowed)).divide(DAILY_RATE_DIVISOR, 2, RoundingMode.HALF_DOWN);
        stLoanCoreBuilder.stCharges(amtLetterCharge, daysIntCharge, amtMaxCharge, amtAdminCharge, loanTermExemptSt, loanTermExemptEnd, amtDailyIntVal);
        stLoanCoreBuilder.loanNo(f263Resp.getBIdentifiers().getLoanAgreementNo());
        return stLoanCoreBuilder.build();
    }

    private St2LoanIllustration getSt2LoanIllustration(F263Resp f263Resp) {
        St2LoanIllustrationBuilder st2LoanIllustrationBuilder = new St2LoanIllustrationBuilder();
        IllustrationDetails illustrationDetails = f263Resp.getIllustrationDetails();
        st2LoanIllustrationBuilder.bIndicativeIllustration(illustrationDetails.getInterestRates().getFinalQteIn().equals("1"));
        BigDecimal amtLoan = convertStringToBigDecimal(illustrationDetails.getLoanAm().getLoanCshAm(), DIVISOR_HUNDRED);
        BigInteger loanTerm = BigInteger.valueOf(illustrationDetails.getLoanTerm().getPrimaryLoanTermDr());
        BigInteger loanTermDefer = BigInteger.valueOf(illustrationDetails.getLoanTerm().getDeferredMonthsNo());
        BigDecimal amtMonthlyLoanPlusInterest = new BigDecimal(illustrationDetails.getMonthlyRepayment().getPriRpymtCshAm()).divide(new BigDecimal(DIVISOR_HUNDRED));
        BigDecimal amtMonthlyRepayment = new BigDecimal(illustrationDetails.getMonthlyRepayment().getPriRpymtCshAm()).divide(new BigDecimal(DIVISOR_HUNDRED));
        BigDecimal amtTotalInterest = convertStringToBigDecimal(illustrationDetails.getInterestPayable().getIntPyblTtlAm(), DIVISOR_HUNDRED);
        BigDecimal amtTotalRepayment = convertStringToBigDecimal(illustrationDetails.getTotalAmountPayable().getTtlPyblTtlAm(), DIVISOR_HUNDRED);
        st2LoanIllustrationBuilder.amountAndTermDetails(amtLoan, loanTerm, loanTermDefer, amtMonthlyLoanPlusInterest, amtMonthlyRepayment, amtTotalRepayment, amtTotalInterest);
        String insuranceProd = illustrationDetails.getProduct().getInsProductNm();
        BigDecimal insAmtWithFee = convertStringToBigDecimal(illustrationDetails.getArrangementFee().getArrgeFeeTtlAm(), DIVISOR_HUNDRED);
        BigDecimal amtTotalInsNoInt = convertStringToBigDecimal(illustrationDetails.getLoanAm().getInsPremAm(), DIVISOR_HUNDRED);
        BigDecimal amtIntOnInsPre = convertStringToBigDecimal(illustrationDetails.getInterestPayable().getIntPyblInsAm(), DIVISOR_HUNDRED);
        BigDecimal amtMnthlyInsPlusInt = convertStringToBigDecimal(illustrationDetails.getMonthlyRepayment().getPriRpymtInsAm(), DIVISOR_HUNDRED);
        BigDecimal totalInsCost = convertStringToBigDecimal(illustrationDetails.getTotalAmountPayable().getTtlPyblInsAm(), DIVISOR_HUNDRED);
        st2LoanIllustrationBuilder.stLoanInsurance(insuranceProd, insAmtWithFee, amtTotalInsNoInt, amtIntOnInsPre, amtMnthlyInsPlusInt, totalInsCost);
        String recommendedAPR = String.format("%07d", (convertStringToBigDecimal(illustrationDetails.getInterestRates().getRecAPRRt(), TEN_THOUSAND).setScale(ROUND_OFF_DECIMAL_PLACES, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(TEN_THOUSAND)).intValue());
        String actualAPR = String.format("%07d", ((convertStringToBigDecimal(illustrationDetails.getInterestRates().getActualAPRRt(), TEN_THOUSAND).setScale(ROUND_OFF_DECIMAL_PLACES, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(TEN_THOUSAND)).intValue()));
        String monthlyIntRate = String.format("%07d", ((convertStringToBigDecimal(illustrationDetails.getInterestRates().getPriMthlyIntPcRt(), TEN_THOUSAND).setScale(ROUND_OFF_DECIMAL_PLACES, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(TEN_THOUSAND)).intValue()));
        String annualIntRate = String.format("%07d", ((convertStringToBigDecimal(illustrationDetails.getInterestRates().getPriAnnualIntPcRt(), TEN_THOUSAND).setScale(ROUND_OFF_DECIMAL_PLACES, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(TEN_THOUSAND)).intValue()));
        st2LoanIllustrationBuilder.stLoanRate(recommendedAPR, actualAPR, null, monthlyIntRate, annualIntRate);
        BigInteger loanTermEarly = BigInteger.valueOf(illustrationDetails.getEarlySettlementEstimates().get(0).getEarlySettMthsQy());
        BigDecimal loanAmtEarly = convertStringToBigDecimal(illustrationDetails.getTotalAmountPayable().getTtlPyblTtlAm(), DIVISOR_HUNDRED);
        BigDecimal amtInsEarly = convertStringToBigDecimal(illustrationDetails.getEarlySettlementEstimates().get(0).getEarlySettInsAm(), DIVISOR_HUNDRED);
        BigDecimal totalAmt = convertStringToBigDecimal(illustrationDetails.getTotalAmountPayable().getTtlPyblTtlAm(), DIVISOR_HUNDRED);
        BigDecimal amtFeeEarly = convertStringToBigDecimal(illustrationDetails.getEarlySettlementEstimates().get(0).getEarlySettFeeAm(), DIVISOR_HUNDRED);
        BigDecimal amtInsRebateEarly = convertStringToBigDecimal(illustrationDetails.getEarlySettlementEstimates().get(0).getEarlySettInsRbtAm(), DIVISOR_HUNDRED);
        List<St2LoanEarly> st2LoanEarlyList = new ArrayList<>();
        St2LoanEarly st2LoanEarly = st2LoanIllustrationBuilder.st2LoanEarly(loanTermEarly, loanAmtEarly, amtInsEarly, totalAmt, amtFeeEarly, amtInsRebateEarly);
        st2LoanEarlyList.add(st2LoanEarly);
        st2LoanIllustrationBuilder.stLoanEarlyList(st2LoanEarlyList);
        return st2LoanIllustrationBuilder.build();
    }

    private BigDecimal convertStringToBigDecimal(String s, String divisor) {
        BigDecimal bigDecimal = null;
        if (s != null) {
            bigDecimal = new BigDecimal(s).divide(new BigDecimal(divisor));
        }
        return bigDecimal;
    }
}
