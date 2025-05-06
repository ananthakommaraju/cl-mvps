package com.lloydsbanking.salsa.activate.administer;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AdministerReferredLookUpDataTest {
    private AdministerReferredLookUpData administerReferredLookUpData;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private DepositArrangement depositArrangement;

    @Before
    public void setUp() {
        administerReferredLookUpData = new AdministerReferredLookUpData();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        administerReferredLookUpData.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        depositArrangement = testDataHelper.createDepositArrangement("95842");
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("001");
    }

    @Test
    public void testGetDeclineSource() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        List<ReferenceDataLookUp> refLookUpList = testDataHelper.createChannelIdLookupData();
        when(administerReferredLookUpData.lookUpValueRetriever.getLookUpValues(any(ArrayList.class), any(String.class))).thenReturn(refLookUpList);
        String referralCode = depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode();
        String declineSource = administerReferredLookUpData.getDeclineSource(referralCode, requestHeader.getChannelId());
        verify(administerReferredLookUpData.lookUpValueRetriever).getLookUpValues(any(ArrayList.class), any(String.class));
        assertEquals("Bank", declineSource);
    }

    @Test
    public void testGetDeclineSourceWithException() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        DataAccessException dataAccessException = mock(DataAccessException.class);
        when(administerReferredLookUpData.lookUpValueRetriever.getLookUpValues(any(ArrayList.class), any(String.class))).thenThrow(dataAccessException);
        String referralCode = depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode();
        administerReferredLookUpData.getDeclineSource(referralCode, requestHeader.getChannelId());
    }

    @Test
    public void testGetReferralCode() {
        List<ReferenceDataLookUp> refLookUpList = testDataHelper.createLookupData();
        assertEquals("SPORI", administerReferredLookUpData.getReferralCode(refLookUpList, "1"));
    }

    @Test
    public void testCheckIfFamilyIDSameAsCreditDecision() {
        Product product = new Product();
        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(new ExtSysProdFamilyIdentifier());
        productFamily.getExtsysprodfamilyidentifier().get(0).setProductFamilyIdentifier("157889");
        product.getAssociatedFamily().add(productFamily);
        List<ProductFamily> productFamilyListFromCreditDecision = new ArrayList<>();
        ProductFamily productFamily1 = new ProductFamily();
        productFamily1.getExtsysprodfamilyidentifier().add(new ExtSysProdFamilyIdentifier());
        productFamily1.getExtsysprodfamilyidentifier().get(0).setProductFamilyIdentifier("015458");
        productFamilyListFromCreditDecision.add(productFamily1);
        productFamilyListFromCreditDecision.add(productFamily);
        assertTrue(administerReferredLookUpData.checkIfFamilyIDSameAsCreditDecision(product, productFamilyListFromCreditDecision));
    }

    @Test
    public void testCheckIfFamilyIDSameAsCreditDecisionWithNullProduct() {
        List<ProductFamily> productFamilyListFromCreditDecision = new ArrayList<>();
        ProductFamily productFamily1 = new ProductFamily();
        productFamily1.getExtsysprodfamilyidentifier().add(new ExtSysProdFamilyIdentifier());
        productFamily1.getExtsysprodfamilyidentifier().get(0).setProductFamilyIdentifier("015458");
        productFamilyListFromCreditDecision.add(productFamily1);
        assertFalse(administerReferredLookUpData.checkIfFamilyIDSameAsCreditDecision(null, productFamilyListFromCreditDecision));
    }

    @Test
    public void testRetrieveLookUpValuesByGroupCodeAndLookUpText() {
        List<String> lookUpTextList = new ArrayList<>();
        List<String> groupCodeList = new ArrayList<>();
        String referralCode = depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode();
        lookUpTextList.add(referralCode);
        groupCodeList.add("REFERRAL_TEAM_GROUPS");
        when(administerReferredLookUpData.lookUpValueRetriever.retrieveLookUpValues(lookUpTextList, requestHeader.getChannelId(), groupCodeList)).thenReturn(testDataHelper.createLookupData());
        List<ReferenceDataLookUp> referenceDataLookUps = administerReferredLookUpData.retrieveLookUpValuesByGroupCodeAndLookUpText(referralCode, requestHeader.getChannelId());
        assertNotNull(referenceDataLookUps);
        assertEquals("ENCRYPT_KEY_GROUP", referenceDataLookUps.get(0).getGroupCode());
    }

    @Test
    public void testRetrieveLookUpValuesByGroupCodeAndLookUpTextWith001() {
        List<String> lookUpTextList = new ArrayList<>();
        List<String> groupCodeList = new ArrayList<>();
        String referralCode = depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode();
        lookUpTextList.add(referralCode);
        List<ReferenceDataLookUp> refLookUpList = new ArrayList<>();
        groupCodeList.add("REFERRAL_TEAM_GROUPS");
        when(administerReferredLookUpData.lookUpValueRetriever.retrieveLookUpValues(lookUpTextList, requestHeader.getChannelId(), groupCodeList)).thenReturn(refLookUpList);
        List<ReferenceDataLookUp> referenceDataLookUps = administerReferredLookUpData.retrieveLookUpValuesByGroupCodeAndLookUpText(referralCode, requestHeader.getChannelId());
        assertNotNull(referenceDataLookUps);
    }

    @Test
    public void testRetrieveLookUpValuesByGroupCodeAndLookUpTextWithException() {
        List<String> lookUpTextList = new ArrayList<>();
        List<String> groupCodeList = new ArrayList<>();
        String referralCode = depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode();
        lookUpTextList.add(referralCode);
        groupCodeList.add("REFERRAL_TEAM_GROUPS");
        DataAccessException dataAccessException = mock(DataAccessException.class);
        when(administerReferredLookUpData.lookUpValueRetriever.retrieveLookUpValues(lookUpTextList, requestHeader.getChannelId(), groupCodeList)).thenThrow(dataAccessException);
        administerReferredLookUpData.retrieveLookUpValuesByGroupCodeAndLookUpText(referralCode, requestHeader.getChannelId());
    }

    @Test
    public void tescheckIfFamilyIDSameAsCreditDecision() {
        assertFalse(administerReferredLookUpData.checkIfFamilyIDSameAsCreditDecision(depositArrangement.getAssociatedProduct(), new ArrayList<ProductFamily>()));
    }
}
