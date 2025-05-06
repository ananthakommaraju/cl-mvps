package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.asm.client.f204.F204ClientImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveFraudDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.*;

import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class FraudDecisionRetrieverTest {

    F204Resp f204Resp;
    FraudDecisionRetriever fraudDecisionRetriever;
    DepositArrangement depositArrangement;
    RequestHeader requestHeader; 
    TestDataHelper testDataHelper;
    

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        fraudDecisionRetriever = new FraudDecisionRetriever();
        fraudDecisionRetriever.f204Client = mock(F204ClientImpl.class);
        fraudDecisionRetriever.f204RequestFactory = mock(RetrieveFraudDecisionRequestFactory.class);
        fraudDecisionRetriever.headerRetriever = new HeaderRetriever();
        fraudDecisionRetriever.exceptionUtility = new ExceptionUtility();
        depositArrangement = new DepositArrangement();
        depositArrangement.setFinancialInstitution(new Organisation());
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setPrimaryInvolvedParty(new Customer());
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().add(new PostalAddress());

        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setRegionCode("12");
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setAreaCode("24");
        requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        requestHeader.setContactPointId("LTB");
        depositArrangement.getAssociatedProduct().setExternalProductHeldIdentifier("30");
        depositArrangement.setArrangementId("1");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setFirstName("firstName");
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setDurationofStay("0707");
    }


    @Test
    public void testGetFraudDecisionIsSuccessfull() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        f204Resp = testDataHelper.createF204Response(0);
        when(fraudDecisionRetriever.f204Client.performFraudCheck(any(F204Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f204Resp);

        F204Resp response = fraudDecisionRetriever.getFraudDecision(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , requestHeader.getContactPointId(), depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), depositArrangement.getArrangementId(),
                depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), depositArrangement.getPrimaryInvolvedParty(), depositArrangement.getPrimaryInvolvedParty().getPostalAddress(), requestHeader);

        assertEquals("0", response.getF204Result().getResultCondition().getReasonCode().toString());
        assertEquals("reasonText", response.getF204Result().getResultCondition().getReasonText());

    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void testGetFraudDecisionThrowsExternalServiceError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        F204Resp f204Resp = testDataHelper.createF204Response(152222);
        when(fraudDecisionRetriever.f204RequestFactory.create(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , requestHeader.getContactPointId(), depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), depositArrangement.getArrangementId(),
                depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), depositArrangement.getPrimaryInvolvedParty(), depositArrangement.getPrimaryInvolvedParty().getPostalAddress())).thenReturn(new TestDataHelper().createF204Req());
        when(fraudDecisionRetriever.f204Client.performFraudCheck(any(F204Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f204Resp);
        fraudDecisionRetriever.getFraudDecision(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , requestHeader.getContactPointId(), depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), depositArrangement.getArrangementId(),
                depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), depositArrangement.getPrimaryInvolvedParty(), depositArrangement.getPrimaryInvolvedParty().getPostalAddress(), requestHeader);
    }

    @Test(expected = ExternalBusinessErrorMsg.class)
    public void testGetFraudDecisionThrowsExternalBusinessError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        F204Resp f204Resp = testDataHelper.createF204Response(159190);
        when(fraudDecisionRetriever.f204RequestFactory.create(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , requestHeader.getContactPointId(), depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), depositArrangement.getArrangementId(),
                depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), depositArrangement.getPrimaryInvolvedParty(), depositArrangement.getPrimaryInvolvedParty().getPostalAddress())).thenReturn(new TestDataHelper().createF204Req());
        when(fraudDecisionRetriever.f204Client.performFraudCheck(any(F204Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f204Resp);
        fraudDecisionRetriever.getFraudDecision(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , requestHeader.getContactPointId(), depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), depositArrangement.getArrangementId(),
                depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), depositArrangement.getPrimaryInvolvedParty(), depositArrangement.getPrimaryInvolvedParty().getPostalAddress(), requestHeader);

    }


}
