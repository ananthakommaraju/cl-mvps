package com.lloydsbanking.salsa.offer.apply.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.asm.f204.objects.DecisionDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Result;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Result;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Result;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class AsmResponseToCustomerScoreConverterTest {

    private AsmResponseToCustomerScoreConverter asmResponseToCustomerScoreConverter;
    private F204Resp f204Resp;
    private F205Resp f205Resp;
    private F424Resp f424Resp;

    @Before
    public void setUp() {
        asmResponseToCustomerScoreConverter = new AsmResponseToCustomerScoreConverter();
        asmResponseToCustomerScoreConverter.highestPriorityReferralCodeEvaluator = mock(HighestPriorityReferralCodeEvaluator.class);
        asmResponseToCustomerScoreConverter.headerRetriever = new HeaderRetriever();
        TestDataHelper dataHelper = new TestDataHelper();
        f204Resp = dataHelper.createF204Response(0);
        f205Resp = dataHelper.createF205Response(0);
        f424Resp = dataHelper.createF424Response(0);
    }

    @Test
    public void testFraudResponseToCustomerScoreConverterDecline() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f204Resp.setASMCreditScoreResultCd("3");
        asmResponseToCustomerScoreConverter.fraudResponseToCustomerScoreConverter(f204Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("3", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());
        verify(asmResponseToCustomerScoreConverter.highestPriorityReferralCodeEvaluator).findHighestPriorityCode("LTB", "ASM_DECLINE_CODE", customerScore.getReferralCode());
    }

    @Test
    public void testFraudResponseToCustomerScoreConverterAccept() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f204Resp.setASMCreditScoreResultCd("1");
        asmResponseToCustomerScoreConverter.fraudResponseToCustomerScoreConverter(f204Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("1", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());
    }

    @Test
    public void testFraudResponseToCustomerScoreConverterRefer() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f204Resp.setASMCreditScoreResultCd("2");
        asmResponseToCustomerScoreConverter.fraudResponseToCustomerScoreConverter(f204Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("2", customerScore.getScoreResult());

        assertTrue(customerScore.getReferralCode().isEmpty());
    }

    @Test
    public void testFraudResponseToCustomerScoreConverterOther() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f204Resp.setASMCreditScoreResultCd("5");
        asmResponseToCustomerScoreConverter.fraudResponseToCustomerScoreConverter(f204Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("3", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());
    }


    @Test
    public void testCreditScoreResponseToCustomerScoreConverter() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("1");
        f205Resp.setASMCreditScoreResultCd("1");
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, "1003", header);
        assertEquals("2", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());

    }

    @Test
    public void testCreditScoreResponseToCustomerScoreConverterForAcceptAndPreviousAppStatusNotRefer() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("1");
        f205Resp.setASMCreditScoreResultCd("1");
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);
        customerScore.getReferralCode().addAll(referralCodeList);
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, "1007", header);
        assertEquals("1", customerScore.getScoreResult());
        assertEquals("2", customerScore.getReferralCode().get(0).getCode());
        assertEquals("desc 2", customerScore.getReferralCode().get(0).getDescription());

    }

    @Test
    public void testCreditScoreResponseToCustomerScoreConverterForReferAndPreviousAppStatusNotAccept() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("1");
        f205Resp.setASMCreditScoreResultCd("1");
        customerScore.getReferralCode().add(new ReferralCode());
        customerScore.getReferralCode().get(0).setCode("1");
        customerScore.getReferralCode().get(0).setDescription("desc 1");
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);
        customerScore.getReferralCode().addAll(referralCodeList);
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, "1002", header);
        assertEquals("1", customerScore.getScoreResult());
        assertEquals("1", customerScore.getReferralCode().get(0).getCode());
        assertEquals("desc 1", customerScore.getReferralCode().get(0).getDescription());
        assertEquals("2", customerScore.getReferralCode().get(1).getCode());
        assertEquals("desc 2", customerScore.getReferralCode().get(1).getDescription());

    }

    @Test
    public void testCreditScoreResponseToCustomerScoreConverterForReferAndPreviousAppStatusRefer() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("2");
        f205Resp.setASMCreditScoreResultCd("2");
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);
        customerScore.getReferralCode().addAll(referralCodeList);
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, "1003", header);
        assertEquals("2", customerScore.getScoreResult());
        assertEquals("2", customerScore.getReferralCode().get(0).getCode());
        assertEquals("desc 2", customerScore.getReferralCode().get(0).getDescription());

    }

    @Test
    public void testCreditScoreResponseToCustomerScoreConverterForOthers() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("5");
        f205Resp.setASMCreditScoreResultCd("5");
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);
        customerScore.getReferralCode().addAll(referralCodeList);
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, "1003", header);
        assertEquals("3", customerScore.getScoreResult());
        assertEquals("2", customerScore.getReferralCode().get(0).getCode());
        assertEquals("desc 2", customerScore.getReferralCode().get(0).getDescription());

    }

    @Test
    public void testCreditScoreResponseToCustomerScoreConverterForDecline() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("3");
        f205Resp.setASMCreditScoreResultCd("3");
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);
        customerScore.getReferralCode().addAll(referralCodeList);
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, "1003", header);
        assertEquals("3", customerScore.getScoreResult());

    }

    @Test
    public void testCreditDecisionResponseToCustomerScoreConverterForCCDecline() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f424Resp.setASMCreditScoreResultCd("3");
        asmResponseToCustomerScoreConverter.creditDecisionResponseToCustomerScoreConverterForCC(f424Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("3", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());
        verify(asmResponseToCustomerScoreConverter.highestPriorityReferralCodeEvaluator).findHighestPriorityCode("LTB", "ASM_DECLINE_CODE", customerScore.getReferralCode());
    }

    @Test
    public void testCreditDecisionResponseToCustomerScoreConverterForCCAccept() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f424Resp.setASMCreditScoreResultCd("1");
        asmResponseToCustomerScoreConverter.creditDecisionResponseToCustomerScoreConverterForCC(f424Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("1", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());
    }

    @Test
    public void testCreditDecisionResponseToCustomerScoreConverterForCCRefer() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f424Resp.setASMCreditScoreResultCd("2");
        asmResponseToCustomerScoreConverter.creditDecisionResponseToCustomerScoreConverterForCC(f424Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("2", customerScore.getScoreResult());

        assertTrue(customerScore.getReferralCode().isEmpty());
    }

    @Test
    public void testCreditDecisionResponseToCustomerScoreConverterForCCOther() throws DataNotAvailableErrorMsg {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        CustomerScore customerScore = new CustomerScore();
        f424Resp.setASMCreditScoreResultCd("5");
        asmResponseToCustomerScoreConverter.creditDecisionResponseToCustomerScoreConverterForCC(f424Resp, customerScore, header);
        assertEquals("0", customerScore.getScoreIdentifier());
        assertEquals("3", customerScore.getScoreResult());
        assertTrue(customerScore.getReferralCode().isEmpty());
    }

    @Test
    public void testFaudResponseToCustomerScoreConverter() throws DataNotAvailableErrorMsg {
        F204Resp f204Resp = new F204Resp();
        f204Resp.setF204Result(new F204Result());
        f204Resp.getF204Result().setResultCondition(new ResultCondition());
        f204Resp.getF204Result().getResultCondition().setSeverityCode((byte) 1);
        f204Resp.setASMCreditScoreResultCd(null);

        DecisionDetails decisionDetails = new DecisionDetails();
        decisionDetails.setCSDecisionReasonTypeCd("20");
        decisionDetails.setCSDecisionReasonTypeNr("40");
        f204Resp.getDecisionDetails().add(decisionDetails);
        CustomerScore customerScore = new CustomerScore();
        RequestHeader header = new RequestHeader();
        header.setChannelId("LTB");
        asmResponseToCustomerScoreConverter.fraudResponseToCustomerScoreConverter(f204Resp,customerScore,header);

        assertEquals("2",customerScore.getScoreResult());
        assertEquals("20",customerScore.getReferralCode().get(0).getCode());
        assertEquals("40",customerScore.getReferralCode().get(0).getDescription());
    }

    @Test
    public void testCreditScoreResponseToCustomerScore() throws DataNotAvailableErrorMsg {
        F205Resp f205Resp = new F205Resp();
        f205Resp.setF205Result(new F205Result());
        f205Resp.getF205Result().setResultCondition(new ResultCondition());
        f205Resp.getF205Result().getResultCondition().setSeverityCode((byte) 1);
        f205Resp.setASMCreditScoreResultCd(null);

        com.lloydsbanking.salsa.soap.asm.f205.objects.DecisionDetails decisionDetails = new com.lloydsbanking.salsa.soap.asm.f205.objects.DecisionDetails();
        decisionDetails.setCSDecisionReasonTypeCd("20");
        decisionDetails.setCSDecisionReasonTypeNr("40");
        f205Resp.getDecisionDetails().add(decisionDetails);
        CustomerScore customerScore = new CustomerScore();
        RequestHeader header = new RequestHeader();
        header.setChannelId("LTB");
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore,"1", header);

        assertEquals("2",customerScore.getScoreResult());
    }

    @Test
    public void testCreditDecisionResponseToCustomerScoreConverterForCC() throws DataNotAvailableErrorMsg {
        F424Resp f424Resp = new F424Resp();
        f424Resp.setF424Result(new F424Result());
        f424Resp.getF424Result().setResultCondition(new ResultCondition());
        f424Resp.getF424Result().getResultCondition().setSeverityCode((byte) 1);
        f424Resp.getF424Result().getResultCondition().setReasonCode(150900);
        f424Resp.setASMCreditScoreResultCd(null);

        com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails decisionDetails = new com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails();

        decisionDetails.setCSDecisionReasonTypeCd("20");
        decisionDetails.setCSDecisionReasonTypeNr("40");
        f424Resp.getDecisionDetails().add(decisionDetails);
        CustomerScore customerScore = new CustomerScore();
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation("LTB", "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        asmResponseToCustomerScoreConverter.creditDecisionResponseToCustomerScoreConverterForCC(f424Resp, customerScore, header);

        assertEquals("2",customerScore.getScoreResult());
    }
}
