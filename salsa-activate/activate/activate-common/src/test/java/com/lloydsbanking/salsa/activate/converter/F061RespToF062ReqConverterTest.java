package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class F061RespToF062ReqConverterTest {
    private F061RespToF062ReqConverter f061RespToF062ReqConverter;

    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        f061RespToF062ReqConverter = new F061RespToF062ReqConverter();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testConvert() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        Customer customer = testDataHelper.createApaRequestForPca().getProductArrangement().getPrimaryInvolvedParty();
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().getIndividualName().add(new IndividualName());
        customer.getIsPlayedBy().getIndividualName().get(0).getMiddleNames().add("BANG");
        customer.getIsPlayedBy().getIndividualName().get(0).getMiddleNames().add("JOSH");
        customer.getIsPlayedBy().setBirthDate(testDataHelper.createXMLGregorianCalendar(1998, 10, 21));
        customer.getIsPlayedBy().setEmploymentStatus("001");
        customer.getIsPlayedBy().setResidentialStatus("1");
        F062Req f062Req = f061RespToF062ReqConverter.convert(testDataHelper.createF061Resp(), assessmentEvidence, customer);
        assertNotNull(f062Req);
        assertEquals("03082015", f062Req.getPartyUpdData().getPersonalUpdData().getDOBAuditUpdData().getAuditDt());
        assertEquals("083703", f062Req.getPartyUpdData().getPersonalUpdData().getDOBAuditUpdData().getAuditTm());
        assertEquals("023546", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditTm());
        assertEquals("10254", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditDt());
        assertEquals("124580", f062Req.getPartyUpdData().getKYCPartyUpdData().getCtyRes().getCountryOfResidCd());
        assertEquals("0", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getStaffIn());
    }

    @Test
    public void testConvertFrstNtn() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        Customer customer = testDataHelper.createApaRequestForPca().getProductArrangement().getPrimaryInvolvedParty();
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().getIndividualName().add(new IndividualName());
        customer.getIsPlayedBy().getIndividualName().get(0).getMiddleNames().add("BANG");
        customer.getIsPlayedBy().getIndividualName().get(0).getMiddleNames().add("JOSH");
        customer.getIsPlayedBy().setBirthDate(testDataHelper.createXMLGregorianCalendar(1998, 10, 21));
        customer.getIsPlayedBy().setEmploymentStatus("001");
        customer.getIsPlayedBy().setResidentialStatus("1");
        customer.getIsPlayedBy().setNationality("GBR");
        F062Req f062Req = f061RespToF062ReqConverter.convert(testDataHelper.createF061RespForKYCNoFrstNtn(), assessmentEvidence, customer);
        assertNotNull(f062Req);
        assertEquals("03082015", f062Req.getPartyUpdData().getPersonalUpdData().getDOBAuditUpdData().getAuditDt());
        assertEquals("083703", f062Req.getPartyUpdData().getPersonalUpdData().getDOBAuditUpdData().getAuditTm());
        assertEquals("023546", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditTm());
        assertEquals("10254", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditDt());
        assertEquals("124580", f062Req.getPartyUpdData().getKYCPartyUpdData().getCtyRes().getCountryOfResidCd());
        assertEquals("GBR", f062Req.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().getFirstNationltyCd());
        assertEquals(null, f062Req.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().getFrstNtnAuditUpdData());
        assertEquals("0", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getStaffIn());
    }

    @Test
    public void testConvertWhenThirdForeNameIsNull() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        Customer customer = testDataHelper.getPrimaryInvolvedParty();
        customer.getIsPlayedBy().getIndividualName().add(new IndividualName());
        customer.getIsPlayedBy().setBirthDate(testDataHelper.createXMLGregorianCalendar(1998, 10, 21));
        F061Resp f061Resp = testDataHelper.createF061Resp();
        f061Resp.getPartyEnqData().getPersonalData().setThirdForeNm(null);
        F062Req f062Req = f061RespToF062ReqConverter.convert(f061Resp, assessmentEvidence, customer);
        assertNotNull(f062Req);
        assertEquals("083703", f062Req.getPartyUpdData().getPersonalUpdData().getDOBAuditUpdData().getAuditTm());
        assertEquals("10254", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditDt());
        assertEquals("GBR", f062Req.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().getFirstNationltyCd());
        assertEquals("", f062Req.getPartyUpdData().getPersonalUpdData().getThirdForeNm());
    }

    @Test
    public void testConvertWithPersonalThirdForeName() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        Customer customer = testDataHelper.getPrimaryInvolvedParty();
        customer.getIsPlayedBy().getIndividualName().add(new IndividualName());
        customer.getIsPlayedBy().setBirthDate(testDataHelper.createXMLGregorianCalendar(1998, 10, 21));
        F062Req f062Req = f061RespToF062ReqConverter.convert(testDataHelper.createF061Resp(), assessmentEvidence, customer);
        assertNotNull(f062Req);
        assertEquals("083703", f062Req.getPartyUpdData().getPersonalUpdData().getDOBAuditUpdData().getAuditTm());
        assertEquals("10254", f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditDt());
        assertEquals("GBR", f062Req.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().getFirstNationltyCd());
        assertEquals("Abcdefhi", f062Req.getPartyUpdData().getPersonalUpdData().getThirdForeNm());
    }

    @Test
    public void testConvertWithNullData() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        Customer customer = testDataHelper.getPrimaryInvolvedParty();
        customer.getIsPlayedBy().getIndividualName().add(new IndividualName());
        customer.getIsPlayedBy().setBirthDate(testDataHelper.createXMLGregorianCalendar(1998, 10, 21));
        F061Resp f061Resp = testDataHelper.createF061Resp();
        f061Resp.getPartyEnqData().setKYCNonCorePartyData(null);
        f061Resp.getPartyEnqData().setPartyNonCoreData(null);
        f061Resp.getPartyEnqData().setPersonalData(null);
        f061Resp.getPartyEnqData().setAddressData(null);
        F062Req f062Req = f061RespToF062ReqConverter.convert(f061Resp, assessmentEvidence, customer);
        assertNotNull(f062Req);
        assertEquals(null, f062Req.getPartyUpdData().getPartyNonCoreUpdData().getPartyDetailAuditUpdData().getAuditDt());
        assertEquals("GBR", f062Req.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().getFirstNationltyCd());
        assertEquals(null, f062Req.getPartyUpdData().getPersonalUpdData().getThirdForeNm());
    }

}
