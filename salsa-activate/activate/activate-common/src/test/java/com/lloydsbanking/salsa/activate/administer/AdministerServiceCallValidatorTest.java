package com.lloydsbanking.salsa.activate.administer;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.sira.SiraReferredService;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AdministerServiceCallValidatorTest {
    AdministerServiceCallValidator administerServiceCallValidator;
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;
    ProductArrangement productArrangement;

    @Before
    public void setUp() {
        administerServiceCallValidator = new AdministerServiceCallValidator();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        administerServiceCallValidator.administerReferredService = mock(AdministerReferredService.class);
        administerServiceCallValidator.siraReferredService = mock(SiraReferredService.class);
        administerServiceCallValidator.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        administerServiceCallValidator.switchClient = mock(SwitchService.class);
        productArrangement = testDataHelper.createDepositArrangement("1001");
        productArrangement.setApplicationStatus("1");
        productArrangement.setRetryCount(1);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement.setGuardianDetails(new Customer());
        productArrangement.getGuardianDetails().getCustomerScore().add(new CustomerScore());
        productArrangement.getOfferedProducts().add(new Product());
        productArrangement.getOfferedProducts().get(0).getProductoffer().add(new ProductOffer());
        administerServiceCallValidator.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(administerServiceCallValidator.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngment");

    }

    @Test
    public void testCheckApplicationStatus() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.UNSCORED.getValue());
        ExtraConditions extraConditions = administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement, requestHeader, "1024");
        assertNull(extraConditions);
    }

    @Test
    public void testCheckApplicationStatusAndCallSiraReferredService() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ProductArrangement productArrangement1 = new ProductArrangement();
        productArrangement1.setApplicationStatus(ApplicationStatus.REFERRED.getValue());
        productArrangement1.setApplicationSubStatus("5001");
        productArrangement1.setArrangementType(ArrangementType.CURRENT_ACCOUNT.getValue());
        productArrangement1.setSIRAEnabledSwitch(true);
        RuleCondition ruleCondition=new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        ruleCondition.setResult("N");
        productArrangement1.getConditions().add(ruleCondition);
        productArrangement1.setSIRAEnabledSwitch(true);
        when(administerServiceCallValidator.siraReferredService.siraReferredArrangement(any(ProductArrangement.class), any(String.class), any(ExtraConditions.class), any(RequestHeader.class))).thenReturn(productArrangement1);
        ExtraConditions extraConditions = administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement1, requestHeader, "1024");
        assertNotNull(extraConditions);
        assertNull(productArrangement.getApplicationSubStatus());
    }

    @Test
    public void testCheckApplicationStatusAndCallAdministerService() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        ProductArrangement productArrangement1 = new ProductArrangement();
        productArrangement1.getReferral().add(new Referral());
        productArrangement1.setApplicationSubStatus("111");
        productArrangement1.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement1.setPrimaryInvolvedParty(new Customer());
        productArrangement1.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement1.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("1");
        productArrangement1.getOfferedProducts().add(new Product());
        productArrangement1.getOfferedProducts().get(0).getProductoffer().add(new ProductOffer());
        productArrangement1.getOfferedProducts().get(0).getProductoffer().get(0).setOfferAmount(new CurrencyAmount());
        when(administerServiceCallValidator.administerReferredService.administerReferredArrangement(any(ProductArrangement.class), any(String.class), any(ExtraConditions.class), any(RequestHeader.class))).thenReturn(productArrangement1);
        ExtraConditions extraConditions = administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement, requestHeader, "1024");
        assertNotNull(extraConditions);
    }

    @Test
    public void testCallAdministerServiceForCurrentAccountArrangementType() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement.setArrangementType(ArrangementType.CURRENT_ACCOUNT.getValue());
        ProductArrangement productArrangement1 = new ProductArrangement();
        productArrangement1.getReferral().add(new Referral());
        productArrangement1.setApplicationSubStatus("111");
        productArrangement1.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement1.setPrimaryInvolvedParty(new Customer());
        productArrangement1.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement1.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("1");
        productArrangement1.getOfferedProducts().add(new Product());
        productArrangement1.getOfferedProducts().get(0).getProductoptions().add(new ProductOptions());
        productArrangement1.getOfferedProducts().get(0).getProductoptions().get(0).setOptionsCode("102");
        productArrangement1.getOfferedProducts().get(0).getProductoptions().get(0).setOptionsValue("0");
        when(administerServiceCallValidator.administerReferredService.administerReferredArrangement(any(ProductArrangement.class), any(String.class), any(ExtraConditions.class), any(RequestHeader.class))).thenReturn(productArrangement1);
        ExtraConditions extraConditions = administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement, requestHeader, "1024");
        assertNotNull(extraConditions);
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testCallAdministerServiceForException() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().clear();
        when(administerServiceCallValidator.administerReferredService.administerReferredArrangement(any(ProductArrangement.class), any(String.class), any(ExtraConditions.class), any(RequestHeader.class))).thenReturn(new ProductArrangement());
        when(administerServiceCallValidator.exceptionUtilityActivate.internalServiceError(any(String.class), any(String.class), any(RequestHeader.class))).thenThrow(ActivateProductArrangementInternalSystemErrorMsg.class);
        administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement, requestHeader, "1024");
    }

    @Test
    public void testCheckApplicationStatusAndCallAdministerServiceNullOfferedProducts() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        ProductArrangement productArrangement1 = new ProductArrangement();
        productArrangement1.getReferral().add(new Referral());
        productArrangement1.setApplicationSubStatus("111");
        productArrangement1.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        productArrangement1.setPrimaryInvolvedParty(new Customer());
        productArrangement1.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement1.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("1");
        when(administerServiceCallValidator.administerReferredService.administerReferredArrangement(any(ProductArrangement.class), any(String.class), any(ExtraConditions.class), any(RequestHeader.class))).thenReturn(productArrangement1);
        ExtraConditions extraConditions = administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement, requestHeader, "1024");
        assertNotNull(extraConditions);
    }

}
