package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Req;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class RetrieveCreditScoreRequestFactoryTest {
    private RetrieveCreditScoreRequestFactory f205RequestFactory;
    private F205Req f205Req;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        f205RequestFactory = new RetrieveCreditScoreRequestFactory();
        testDataHelper = new TestDataHelper();
        f205Req = null;
    }

    @Test
    public void testCreate() throws ParseException, DatatypeConfigurationException {
        Individual isPlayedBy = testDataHelper.createIsPlayedBy();
        isPlayedBy.setMaritalStatus("003");
        isPlayedBy.setEmploymentStatus("000");
        isPlayedBy.setGender("003");
        isPlayedBy.setIsStaffMember(true);
        isPlayedBy.setOccupation("occupation");
        isPlayedBy.setNumberOfDependents(BigInteger.valueOf(3));
        isPlayedBy.setResidentialStatus("resiStatus");
        isPlayedBy.setCurrentYearOfStudy(BigInteger.valueOf(1992));
        Date sampleDate = (new SimpleDateFormat("yyyyMMdd")).parse("19921213");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        isPlayedBy.setUKResidenceStartDate(calendar);
        isPlayedBy.setBirthDate(calendar);
        isPlayedBy.setAnticipateDateOfGraduation("1991");

        List<TelephoneNumber> telephoneNumberList = new ArrayList<>();
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setTelephoneType("1");
        telephoneNumber.setAreaCode("areaCode");
        telephoneNumber.setCountryPhoneCode("countryCode");
        telephoneNumber.setPhoneNumber("telephoneNumber");
        telephoneNumberList.add(telephoneNumber);

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setDurationofStay("0308");
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("addressline1");
        unstructuredAddress.setAddressLine2("addressline2");
        unstructuredAddress.setAddressLine3("addressline3");
        unstructuredAddress.setAddressLine4("addressline4");
        unstructuredAddress.setAddressLine5("addressline5");
        unstructuredAddress.setAddressLine6("addressline6");
        unstructuredAddress.setAddressLine7("addressline7");
        unstructuredAddress.setAddressLine8("UK");
        postalAddress.setIsPAFFormat(false);
        postalAddress.setUnstructuredAddress(unstructuredAddress);
        postalAddress.setIsBFPOAddress(true);
        List<PostalAddress> postalAddressList = new ArrayList<>();
        postalAddressList.add(postalAddress);

        Customer primaryInvolvedParty = new Customer();
        primaryInvolvedParty.setSourceSystemId("1234");
        primaryInvolvedParty.setCustomerIdentifier("122323");
        primaryInvolvedParty.setCbsCustomerNumber("cbsCustomerNo");
        primaryInvolvedParty.setCidPersID("5678");
        primaryInvolvedParty.setCustomerSegment("cusseg");
        primaryInvolvedParty.setOtherBankDuration("0708");
        primaryInvolvedParty.setIsPlayedBy(isPlayedBy);
        primaryInvolvedParty.getTelephoneNumber().addAll(telephoneNumberList);
        primaryInvolvedParty.getPostalAddress().addAll(postalAddressList);

        f205Req = f205RequestFactory.create(testDataHelper.TEST_RETAIL_CHANNEL_ID, "arrangementId", "associatedproductbrandname", testDataHelper.createExtSysProdId(), primaryInvolvedParty, "areaCd", "regionCd", "1", testDataHelper.createExistingProducts(), testDataHelper.createRuleConditionList());

        assertEquals(0, f205Req.getMaxRepeatGroupQy());
        assertEquals("012", f205Req.getRequestDetails().getSourceSystemCd());
        assertEquals("004", f205Req.getRequestDetails().getApplicationSourceCd());
        assertEquals("N", f205Req.getFunctionDetails().getAddressTargetingIn());
        assertEquals("N", f205Req.getFunctionDetails().getAddressCorrectionIn());
        assertEquals("GBP", f205Req.getFunctionDetails().getCurrencyCd());
        assertEquals("", f205Req.getFunctionDetails().getCSAdditionalDataCd());
        assertEquals("", f205Req.getFunctionDetails().getBureauReferenceId());
        assertEquals("013", f205Req.getApplicationDetails().getMarketingCd());
        assertEquals("", f205Req.getApplicationDetails().getCampaignNm());
        assertEquals("", f205Req.getApplicationDetails().getCampaignSegmentCd());
        assertEquals("N", f205Req.getApplicationDetails().getCSNewPartyIn());

        assertEquals("arrangementId", f205Req.getRequestDetails().getCreditScoreRequestNo());

        assertEquals(201, f205Req.getApplicationDetails().getProductId());

        assertEquals("regionCd", f205Req.getRequestDetails().getRegionCd());
        assertEquals("areaCd", f205Req.getRequestDetails().getAreaCd());
        assertEquals("LTB", f205Req.getRequestDetails().getSortCd());
        assertEquals("N", f205Req.getApplicationDetails().getCSSecondaryAccountIn());


        assertEquals("000", f205Req.getPersonalDetails().get(0).getEmploymentStatusCd());
        assertEquals("", f205Req.getPersonalDetails().get(0).getSocioEconomicGroupCd());
        assertEquals(0, f205Req.getPersonalDetails().get(0).getLastEmployedAg());
        assertEquals(0, f205Req.getPersonalDetails().get(0).getPreviousEmploymentDr());
        assertEquals("", f205Req.getPersonalDetails().get(0).getPnsnblEmplytIn());
        assertEquals("", f205Req.getPersonalDetails().get(0).getStudntFinnclSupprtTypeCd());
        assertEquals("0", f205Req.getPersonalDetails().get(0).getResidentialPropertyVl());
        assertEquals("0", f205Req.getPersonalDetails().get(0).getOutstandingMortgageAm());

        assertEquals("Y", f205Req.getPersonalDetails().get(0).getIncomeExpenditureIn());

        assertEquals("N", f205Req.getPersonalDetails().get(0).getLTSBChequeCardHeldIn());
        assertEquals("", f205Req.getPersonalDetails().get(0).getLTSBCreditCardExprncCd());
        assertEquals("0", f205Req.getPersonalDetails().get(0).getLTSBCreditCardLimitAm());
        assertEquals("N", f205Req.getPersonalDetails().get(0).getLTSBMortgageHeldIn());
        assertEquals("N", f205Req.getPersonalDetails().get(0).getLTSBCreditCardHeldIn());
        assertEquals(0, f205Req.getPersonalDetails().get(0).getCreditCardHeldQy());
        assertEquals("N", f205Req.getPersonalDetails().get(0).getStoreCardHeldIn());
        assertEquals("N", f205Req.getPersonalDetails().get(0).getChargeCardHeldIn());

        assertEquals(1234, f205Req.getPersonalDetails().get(0).getPartyIdentifiers().get(0).getExtSysId());
        assertEquals("5678", f205Req.getPersonalDetails().get(0).getPartyIdentifiers().get(0).getExtPartyIdTx());

        assertEquals("firstname", f205Req.getPersonalDetails().get(0).getFirstForeNm());
        assertEquals("m", f205Req.getPersonalDetails().get(0).getSecondIt());
        assertEquals("", f205Req.getPersonalDetails().get(0).getThirdIt());
        assertEquals("", f205Req.getPersonalDetails().get(0).getFourthIt());
        assertEquals("lastname", f205Req.getPersonalDetails().get(0).getSurname());
        assertEquals("Mr", f205Req.getPersonalDetails().get(0).getPartyTl());

        assertEquals("003", f205Req.getPersonalDetails().get(0).getMaritalStatusCd());
        assertEquals(707, f205Req.getPersonalDetails().get(0).getCurrentEmploymentDr());
        assertEquals("000", f205Req.getPersonalDetails().get(0).getEmploymentStatusCd());
        assertEquals(122323, f205Req.getPersonalDetails().get(0).getPartyId());
        assertEquals("003", f205Req.getPersonalDetails().get(0).getGenderCd());
        assertEquals("cusseg", f205Req.getPersonalDetails().get(0).getPartyBusinessRltnspCd());
        assertEquals("Y", f205Req.getPersonalDetails().get(0).getPartyStaffIn());
        assertEquals("occupation", f205Req.getPersonalDetails().get(0).getOccupationRoleCd());
        assertEquals(3, f205Req.getPersonalDetails().get(0).getPartyDpndntChildrnQy());
        assertEquals("resiStatus", f205Req.getPersonalDetails().get(0).getResidentialStatusCd());
        assertEquals("1992", f205Req.getPersonalDetails().get(0).getStudentStudyYr());

        assertEquals("13121992", f205Req.getPersonalDetails().get(0).getBirthDt());
        assertEquals((short) 1991, f205Req.getPersonalDetails().get(0).getGraduationAg());
        assertEquals("199212", f205Req.getPersonalDetails().get(0).getUKResidencyStartDt());
        assertEquals("Y", f205Req.getPersonalDetails().get(0).getOtherBankIn());

        assertEquals("003", f205Req.getPersonalDetails().get(0).getSavingsHeldCd());
        assertEquals("300", f205Req.getPersonalDetails().get(0).getSavingsAm());
        assertEquals("001", f205Req.getPersonalDetails().get(0).getCustomerOriginCd());

        assertEquals("areaCode", f205Req.getPersonalDetails().get(0).getHomeTelephoneSTDNo());
        assertEquals("telephoneNumber", f205Req.getPersonalDetails().get(0).getHomeTelephoneNo());
        assertEquals("Y", f205Req.getPersonalDetails().get(0).getHomeTelephoneNumberIn());

        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankCd());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankClosureReasonCd());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankPerformanceCd());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankStatusCd());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankChequeCardHeldIn());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankCurrentAccountIn());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankDepositAccountIn());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankDebitCardIn());
        assertEquals("", f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankLoanIn());

        assertEquals(708, f205Req.getPersonalDetails().get(0).getOtherBankDetails().getOtherBankAssctnDr());

        assertEquals("addressline1", f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getUnformattedAddress().getAddressFirstLineTx());
        assertEquals("addressline2", f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getUnformattedAddress().getAddressSecondLineTx());
        assertEquals("addressline4", f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getUnformattedAddress().getAddressFourthLineTx());
        assertEquals("addressline3", f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getUnformattedAddress().getAddressThirdLineTx());
        assertEquals("002", f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getUnformattedAddress().getUnstrdPostalAddrssTypeCd());

        assertEquals("", f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getAddressResolutionCd());
        assertEquals(308, f205Req.getPersonalDetails().get(0).getAddressDetails().get(0).getAddressResidenceDr());

        assertEquals("4", f205Req.getPersonalDetails().get(0).getIncomeExpenditureDetails().getPeriodNetIncomeFrqncyCd());
        assertEquals("300", f205Req.getPersonalDetails().get(0).getIncomeExpenditureDetails().getPeriodNetIncomeAm());
        assertEquals("300", f205Req.getPersonalDetails().get(0).getIncomeExpenditureDetails().getMnthlyAccmmnPaymntAm());
        assertEquals("3600", f205Req.getPersonalDetails().get(0).getIncomeExpenditureDetails().getOtherAnnualIncomeAm());
        assertEquals("300", f205Req.getPersonalDetails().get(0).getIncomeExpenditureDetails().getLoanCmmtmtMnthlyAm());
        assertEquals("Y", f205Req.getPersonalDetails().get(0).getIncomeExpenditureDetails().getMandatedSalaryIn());

    }


}
