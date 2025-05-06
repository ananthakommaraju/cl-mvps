package com.lloydsbanking.salsa.apasa.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.downstream.CreateAccountRetriever;
import com.lloydsbanking.salsa.activate.downstream.RetrieveProductFeatures;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.postfulfil.AsyncProcessFulfilmentActivitiesCaller;
import com.lloydsbanking.salsa.apasa.TestDataHelper;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.MapProductArrangementToDepositArrangement;
import com.lloydsbanking.salsa.apasa.service.fulfil.downstream.AmendRollOverAccount;
import com.lloydsbanking.salsa.apasa.service.fulfil.downstream.CreateStandingOrder;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class FulfilPendingSavingsArrangementTest {
    FulfilPendingSavingsArrangement fulfilPendingSavingsArrangement;

    private TestDataHelper testDataHelper;

    private RequestHeader requestHeader;

    private DepositArrangement depositArrangement;
    private ActivateProductArrangementRequest request;
    ApplicationDetails applicationDetails;
    ActivateProductArrangementResponse response;
    Map<String, String> accountMap;
    Product product;
    ContactPoint contactPoint;

    @Before
    public void setUp() {
        accountMap = new HashMap<>();
        contactPoint = new ContactPoint();
        contactPoint.setContactPointId("1234567890");
        response = new ActivateProductArrangementResponse();
        fulfilPendingSavingsArrangement = new FulfilPendingSavingsArrangement();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        fulfilPendingSavingsArrangement.retrieveProductFeatures = mock(RetrieveProductFeatures.class);
        depositArrangement = testDataHelper.createDepositArrangement("95412");
        request = testDataHelper.createApaRequestForSa();
        applicationDetails = new ApplicationDetails();
        fulfilPendingSavingsArrangement.createAccountRetriever = mock(CreateAccountRetriever.class);
        fulfilPendingSavingsArrangement.appGroupRetriever = mock(AppGroupRetriever.class);
        fulfilPendingSavingsArrangement.headerRetriever = new HeaderRetriever();
        fulfilPendingSavingsArrangement.createStandingOrder = mock(CreateStandingOrder.class);
        fulfilPendingSavingsArrangement.amendRollOverAccount = mock(AmendRollOverAccount.class);
        fulfilPendingSavingsArrangement.asyncProcessFulfilmentActivitiesCaller = mock(AsyncProcessFulfilmentActivitiesCaller.class);
        fulfilPendingSavingsArrangement.updatePamServiceForActivateDA = mock(UpdatePamServiceForActivateDA.class);
        fulfilPendingSavingsArrangement.mapProductArrangementToDepositArrangement = new MapProductArrangementToDepositArrangement();
        product = new Product();
        fulfilPendingSavingsArrangement.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(fulfilPendingSavingsArrangement.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");
    }

    @Test
    public void testFulfillPendingSavingAccountArrangement() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testStandingOrderCall() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement = testDataHelper.createDepositArrangementFor502("95412");
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "09")).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        // when(fulfilPendingSavingsArrangement.createAccountRetriever.createAccount(requestHeader, depositArrangement, product, accountMap, response,applicationDetails)).thenReturn(applicationDetails);
        request.setProductArrangement(depositArrangement);
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testCreateAccountAndAmendRollover() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setIsSecondaryAccount(true);
        request.setProductArrangement(depositArrangement);
        ExtraConditions extraConditions = new ExtraConditions();
        extraConditions.getConditions().add(new Condition());
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        when(fulfilPendingSavingsArrangement.amendRollOverAccount.amendRollOverAccount(any(DepositArrangement.class), any(RequestHeader.class))).thenReturn(extraConditions);
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForAwaitingCRSFulfillmentFailureAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.AWAITING_CRS_FULFILLMENT_FAILURE);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForMarketingUpdatePREFFailureAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForPartyRelationshipUpdateFailureAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.PARTY_RELATIONSHIP_UPDATE_FAILURE);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForFailedToUpdateEmailAddressAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForFailedToUpdateNINumberAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_NI_NUMBER);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForUpdateCustomerRecordFailureAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testForCustomerDetailsUpdateFailureAppSubStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE);
        request.setProductArrangement(depositArrangement);
        when(fulfilPendingSavingsArrangement.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class))).thenReturn("09");
        when(fulfilPendingSavingsArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(new Product());
        fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountMap, request);
        verify(fulfilPendingSavingsArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }
}
