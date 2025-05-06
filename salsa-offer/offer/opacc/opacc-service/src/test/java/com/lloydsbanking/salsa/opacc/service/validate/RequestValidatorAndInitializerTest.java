package com.lloydsbanking.salsa.opacc.service.validate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import junit.framework.Assert;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RequestValidatorAndInitializerTest {

    private RequestValidatorAndInitializer validatorAndInitializer;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() {
        validatorAndInitializer = new RequestValidatorAndInitializer();
        validatorAndInitializer.exceptionUtility = new ExceptionUtility();
        validatorAndInitializer.headerRetriever = new HeaderRetriever();
        validatorAndInitializer.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        validatorAndInitializer.responseHeaderConverter = new RequestToResponseHeaderConverter();
        validatorAndInitializer.legalEntityMapUtility = mock(LegalEntityMapUtility.class);
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testValidateRequestSuccessful() throws InternalServiceErrorMsg, ExternalBusinessErrorMsg {
        validatorAndInitializer.validateRequest(dataHelper.generateOfferProductArrangementPCCRequest("LTB"));
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testValidateRequestInternalServiceError() throws InternalServiceErrorMsg, ExternalBusinessErrorMsg {
        validatorAndInitializer.validateRequest(null);
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testValidateRequestExternalBusinessError() throws InternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setPrimaryInvolvedParty(null);
        validatorAndInitializer.validateRequest(request);
    }

    @Test
    public void testIsUnAuthCustomerTrue() {
        assertTrue(validatorAndInitializer.isUnAuthCustomer(dataHelper.createOpaccRequestHeaderForUnAuth("LTB")));
    }

    @Test
    public void testIsUnAuthCustomerFalse() {
        assertFalse(validatorAndInitializer.isUnAuthCustomer(dataHelper.createOpaccRequestHeader("LTB")));
    }

    @Test
    public void testInitialiseVariables() throws DataNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        RequestHeader header= dataHelper.createOpaccRequestHeader("LTB");
        header.setChannelId(null);
        request.setHeader(header);
        when(validatorAndInitializer.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        validatorAndInitializer.initialiseVariables(request, response);
        assertEquals("LTB", request.getHeader().getChannelId());
        assertEquals("LTB", request.getProductArrangement().getAssociatedProduct().getBrandName());
    }

    @Test(expected = OfferException.class)
    public void testInitialiseVariablesDataNotAvailableErrorMsg() throws DataNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest request = new OfferProductArrangementRequest();
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        validatorAndInitializer.headerRetriever = mock(HeaderRetriever.class);
        when(validatorAndInitializer.headerRetriever.getContactPoint(any(RequestHeader.class))).thenThrow(DataNotAvailableErrorMsg.class);
        validatorAndInitializer.initialiseVariables(request, response);
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testValidateRequestWhenProductArrangementIsNull() throws InternalServiceErrorMsg, ExternalBusinessErrorMsg {
        OfferProductArrangementRequest request = new OfferProductArrangementRequest();
        validatorAndInitializer.validateRequest(request);
    }

    @Test
    public void testIsBfpoAddress(){

        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsBFPOAddress(true);
        postalAddressList.add(postalAddress);
        boolean isBfpo = validatorAndInitializer.isBfpoAddress(postalAddressList);
        Assert.assertTrue(isBfpo);
    }

    @Test
    public void testIsNotBfpoAddress(){

        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("saving");
        postalAddress.setIsBFPOAddress(true);
        postalAddressList.add(postalAddress);
        boolean isBfpo = validatorAndInitializer.isBfpoAddress(postalAddressList);
        Assert.assertFalse(isBfpo);
    }


    @Test
    public void testInitialiseVariablesIfRelatedApplicationPresent() throws DataNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        RequestHeader header= dataHelper.createOpaccRequestHeader("LTB");
        header.setChannelId(null);
        request.setHeader(header);
        request.getProductArrangement().setRelatedApplicationId("12345");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(dataHelper.createCustomerScoreList());
        when(validatorAndInitializer.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        validatorAndInitializer.initialiseVariables(request, response);
        assertEquals(1,request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());

    }


    @Test
    public void testInitialiseVariablesIfRelatedApplicationNotPresent() throws DataNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        RequestHeader header= dataHelper.createOpaccRequestHeader("LTB");
        header.setChannelId(null);
        request.setHeader(header);
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(dataHelper.createCustomerScoreList());
        when(validatorAndInitializer.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        validatorAndInitializer.initialiseVariables(request, response);
        assertEquals(0,request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());

    }
}
