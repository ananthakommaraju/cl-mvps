package com.lloydsbanking.salsa.activate.postfulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.downstream.CustomerSegmentCheckService;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.downstream.UpdateMarketingPreferences;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.postfulfil.downstream.*;
import com.lloydsbanking.salsa.activate.postfulfil.rules.ValidateProcessPostFulfilment;
import com.lloydsbanking.salsa.activate.registration.downstream.ActivateIBApplication;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ProcessPostFulfilmentActivitiesServiceTest {
    private TestDataHelper testDataHelper;

    private ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService;

    ActivateProductArrangementRequest request;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        processPostFulfilmentActivitiesService = new ProcessPostFulfilmentActivitiesService();
        request = testDataHelper.createApaRequestForPca();
        processPostFulfilmentActivitiesService.activateIBApplication = mock(ActivateIBApplication.class);
        processPostFulfilmentActivitiesService.updateEmailAddress = mock(UpdateEmailAddress.class);
        processPostFulfilmentActivitiesService.updateNationalInsuranceNumber = mock(UpdateNationalInsuranceNumber.class);
        processPostFulfilmentActivitiesService.customerSegmentCheckService = mock(CustomerSegmentCheckService.class);
        processPostFulfilmentActivitiesService.validateProcessPostFulfilment = new ValidateProcessPostFulfilment();
        processPostFulfilmentActivitiesService.validateProcessPostFulfilment.switchClient = mock(SwitchService.class);
        processPostFulfilmentActivitiesService.updateMarketingPreferences = mock(UpdateMarketingPreferences.class);
        processPostFulfilmentActivitiesService.addInterPartyRelationship = mock(AddInterPartyRelationship.class);
        processPostFulfilmentActivitiesService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        processPostFulfilmentActivitiesService.activateBenefitArrangement = mock(ActivateBenefitArrangement.class);
        processPostFulfilmentActivitiesService.communicatePostFulfilmentActivities = mock(CommunicatePostFulfilmentActivities.class);
        processPostFulfilmentActivitiesService.recordCustomerDetails = mock(RecordCustomerDetails.class);
        processPostFulfilmentActivitiesService.updatePamServiceForActivateDA = mock(UpdatePamServiceForActivateDA.class);
        processPostFulfilmentActivitiesService.updatePamService = mock(UpdatePamService.class);
    }

    @Test
    public void verifyProcessPostFulfilmentActivitiesResponseForUltraLiteValue() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        StB751BAppPerCCRegAuth activateIBApplicationResponse = testDataHelper.createB751Response();
        activateIBApplicationResponse.setTacver(-5);
        when(processPostFulfilmentActivitiesService.activateIBApplication.retrieveActivateIBApplication(request.getProductArrangement(), request.getHeader())).thenReturn(activateIBApplicationResponse);
        processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        verify(processPostFulfilmentActivitiesService.communicatePostFulfilmentActivities).communicateWelcomeMessageAndFundReminder(request.getProductArrangement(), request.getHeader(), request.getHeader().getChannelId(), request.getSourceSystemIdentifier());
    }


    @Test
    public void verifyProcessPostFulfilmentActivitiesResponseForLiteValue() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        StB751BAppPerCCRegAuth activateIBApplicationResponse = testDataHelper.createB751Response();
        activateIBApplicationResponse.setTacver(-4);
        when(processPostFulfilmentActivitiesService.activateIBApplication.retrieveActivateIBApplication(request.getProductArrangement(), request.getHeader())).thenReturn(activateIBApplicationResponse);
        when(processPostFulfilmentActivitiesService.validateProcessPostFulfilment.switchClient.getGlobalSwitchValue("SW_FATCAupdate", "LTB", false)).thenReturn(true);
        processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        verify(processPostFulfilmentActivitiesService.communicatePostFulfilmentActivities).communicateWelcomeMessageAndFundReminder(request.getProductArrangement(), request.getHeader(), request.getHeader().getChannelId(), request.getSourceSystemIdentifier());
    }

    @Test
    public void verifyProcessPostFulfilmentActivitiesToUpdateEmailAddress() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        request.getProductArrangement().setApplicationSubStatus("1030");
        processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        verify(processPostFulfilmentActivitiesService.updateEmailAddress).updateEmail(any(ProductArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class));
        verify(processPostFulfilmentActivitiesService.updateNationalInsuranceNumber, times(0)).updateNationalInsNumber(any(ProductArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class));
    }

    @Test
    public void verifyProcessPostFulfilmentActivitiesToUpdatePreference() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        request.getProductArrangement().setApplicationSubStatus("1029");
        request.getProductArrangement().setArrangementType("CA");
        processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        verify(processPostFulfilmentActivitiesService.updateMarketingPreferences).marketingPreferencesUpdate(any(RequestHeader.class), any(ProductArrangement.class), any(ApplicationDetails.class));
    }

    @Test
    public void verifyProcessPostFulfilmentActivitiesForRefDataLookup() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("LIFE_STYLE_BENEFIT_CODE");
        List<RuleCondition> ruleConditions = new ArrayList<>();
        ruleConditions.add(ruleCondition);
        request.getProductArrangement().getConditions().add(ruleCondition);
        ReferenceDataLookUp lookUp = new ReferenceDataLookUp();
        lookUp.setLookupValueDesc("lookup");
        lookUp.setGroupCode("LIFE_STYLE_BENEFIT");
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(lookUp);
        StB751BAppPerCCRegAuth activateIBApplicationResponse = testDataHelper.createB751Response();
        activateIBApplicationResponse.setTacver(-5);
        when(processPostFulfilmentActivitiesService.activateIBApplication.retrieveActivateIBApplication(request.getProductArrangement(), request.getHeader())).thenReturn(activateIBApplicationResponse);
        when(processPostFulfilmentActivitiesService.lookUpValueRetriever.retrieveLookUpValues(any(List.class), any(String.class), any(List.class))).thenReturn(referenceDataLookUpList);
        processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        verify(processPostFulfilmentActivitiesService.communicatePostFulfilmentActivities).communicateWelcomeMessageAndFundReminder(request.getProductArrangement(), request.getHeader(), request.getHeader().getChannelId(), request.getSourceSystemIdentifier());
    }

    @Test
    public void verifyProcessPostFulfilmentActivitiesForRetDataLookup() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp lookUp = new ReferenceDataLookUp();
        lookUp.setGroupCode("ISO_COUNTRY_CODE");
        lookUp.setLookupText("lookup");
        lookUp.setLookupValueDesc("description");
        referenceDataLookUpList.add(lookUp);
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("ISO_COUNTRY_CODE");
        StB751BAppPerCCRegAuth activateIBApplicationResponse = testDataHelper.createB751Response();
        activateIBApplicationResponse.setTacver(-5);
        when(processPostFulfilmentActivitiesService.activateIBApplication.retrieveActivateIBApplication(request.getProductArrangement(), request.getHeader())).thenReturn(activateIBApplicationResponse);
        when(processPostFulfilmentActivitiesService.lookUpValueRetriever.retrieveLookUpValues(request.getHeader(), groupCodes)).thenReturn(referenceDataLookUpList);
        processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        verify(processPostFulfilmentActivitiesService.communicatePostFulfilmentActivities).communicateWelcomeMessageAndFundReminder(request.getProductArrangement(), request.getHeader(), request.getHeader().getChannelId(), request.getSourceSystemIdentifier());
    }
}
