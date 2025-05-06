package com.lloydsbanking.salsa.eligibility.service.rules.agt;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.ChannelSpecificArrangements;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CheckLoanAndCurrentAccountTypeTest {
    private TestDataHelper testDataHelper;

    private CheckLoanAndCurrentAccountType checkLoanAndCurrentAccountType;
    DetermineElegibileInstructionsRequest upstreamRequest;
    @Before
    public void setUp() {
        checkLoanAndCurrentAccountType = new CheckLoanAndCurrentAccountType();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, "IBV", testDataHelper.TEST_CONTACT_POINT_ID);
        checkLoanAndCurrentAccountType.channelSpecificArrangements = mock(ChannelSpecificArrangements.class);
        checkLoanAndCurrentAccountType.channelToBrandMapping = mock(ChannelToBrandMapping.class);

    }

    @Test
    public void testHasFEPSLoanIsSuccessful() {
        List<ProductPartyData> productPartyDatas = new ArrayList<>();

        ProductPartyData productPartyData = new ProductPartyData();
        productPartyData.setProdGroupId(2);
        productPartyDatas.add(productPartyData);

        assertEquals(true, checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas));
    }

    @Test
    public void testHasFEPSLoanIsUnSuccessful() {
        List<ProductPartyData> productPartyDatas = new ArrayList<>();

        ProductPartyData productPartyData = new ProductPartyData();
        productPartyData.setProdGroupId(1);
        productPartyDatas.add(productPartyData);

        assertEquals(false, checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas));

    }

    @Test
    public void testExistingCreditCardGroupCodeProductIsSuccessful() {
        List<ProductPartyData> productPartyDatas = new ArrayList<>();

        ProductPartyData productPartyData = new ProductPartyData();

        productPartyData.setProdGroupId(3);


        productPartyDatas.add(productPartyData);
        assertEquals(1, checkLoanAndCurrentAccountType.existingCreditCardGroupCodeProduct(productPartyDatas));

    }

    @Test
    public void testExistingCreditCardGroupCodeProductIsUnsuccessful() {
        List<ProductPartyData> productPartyDatas = new ArrayList<>();

        ProductPartyData productPartyData = new ProductPartyData();


        productPartyData.setProdGroupId(1);

        productPartyDatas.add(productPartyData);
        assertEquals(0, checkLoanAndCurrentAccountType.existingCreditCardGroupCodeProduct(productPartyDatas));

    }

    @Test
    public void testHasCurrentAccountOrLoggedInChannelLoanIsSuccessful() throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, EligibilityException {
        List<StAccountListDetail> productArrangements = new ArrayList<>();

        StAccountListDetail stAccountListDetail = new StAccountListDetail();
        stAccountListDetail.setAccountcategory("C");
        stAccountListDetail.setBrandcode("VTB");
        productArrangements.add(stAccountListDetail);

        String channel = upstreamRequest.getHeader().getChannelId();
        when(checkLoanAndCurrentAccountType.channelToBrandMapping.getBrandForChannel(channel)).thenReturn("VER");
        when(checkLoanAndCurrentAccountType.channelSpecificArrangements.getChannelSpecificArrangements(upstreamRequest.getHeader())).thenReturn(productArrangements);
        assertEquals(true, checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C"));

    }

    @Test
    public void testHasCurrentAccountOrLoggedInChannelLoanIsUnSuccessful() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        List<StAccountListDetail> productArrangements = new ArrayList<>();

        StAccountListDetail stAccountListDetail = new StAccountListDetail();
        stAccountListDetail.setAccountcategory("C");
        stAccountListDetail.setBrandcode("VTB");

        productArrangements.add(stAccountListDetail);

        String channel = upstreamRequest.getHeader().getChannelId();
        when(checkLoanAndCurrentAccountType.channelToBrandMapping.getBrandForChannel(channel)).thenReturn("VER");
        when(checkLoanAndCurrentAccountType.channelSpecificArrangements.getChannelSpecificArrangements(upstreamRequest.getHeader())).thenReturn(productArrangements);
        assertEquals(false, checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "S"));

    }
}