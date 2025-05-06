package com.lloydsbanking.salsa.opaloans.service.identify;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.identify.convert.F061ToInvolvedPartyDetailsResponseConverter;
import com.lloydsbanking.salsa.offer.identify.downstream.InvolvedPartyRetriever;
import com.lloydsbanking.salsa.offer.identify.downstream.ProductHoldingRetriever;
import com.lloydsbanking.salsa.offer.identify.evaluate.KYCStatusEvaluator;
import com.lloydsbanking.salsa.offer.identify.utility.CustomerUtility;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydsbanking.salsa.opaloans.service.identify.evaluate.CustomerEvaluator;
import com.lloydsbanking.salsa.opaloans.service.identify.evaluate.ProductEvaluator;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_gmo.exception.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class IdentifyServiceTest {
    IdentifyService involvedPartyDetails;

    RequestHeader header;

    TestDataHelper dataHelper;

    ProductArrangement productArrangement;

    @Before
    public void setUp() {
        involvedPartyDetails = new IdentifyService();
        dataHelper = new TestDataHelper();
        header = dataHelper.createOpaLoansRequestHeader("IBL");
        productArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        involvedPartyDetails.productHoldingRetriever = mock(ProductHoldingRetriever.class);
        involvedPartyDetails.customerUtility = mock(CustomerUtility.class);
        involvedPartyDetails.kycStatusEvaluator = mock(KYCStatusEvaluator.class);
        involvedPartyDetails.f061ToInvolvedPartyDetailsResponseConverter = mock(F061ToInvolvedPartyDetailsResponseConverter.class);
        involvedPartyDetails.involvedPartyRetriever = mock(InvolvedPartyRetriever.class);
        involvedPartyDetails.customerEvaluator = mock(CustomerEvaluator.class);
        involvedPartyDetails.switchClient = mock(SwitchService.class);
        involvedPartyDetails.productEvaluator = mock(ProductEvaluator.class);
    }

    @Test
    public void testIdentifyInvolvedPartyWhenCustomerIdentifierIsPresent() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("11221132");
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("1");
        when(involvedPartyDetails.productHoldingRetriever.getProductHoldings(header, "11221132")).thenReturn(getProductHoldings());
        when(involvedPartyDetails.customerUtility.getCBSCustomerNumber(getProductHoldings())).thenReturn("3214");
        when(involvedPartyDetails.customerUtility.getCustomerSegment(getProductHoldings())).thenReturn("2");
        when(involvedPartyDetails.customerUtility.getFDICustomerID(getProductHoldings())).thenReturn("2201");
        when(involvedPartyDetails.involvedPartyRetriever.retrieveInvolvedPartyDetails(header, "11221132")).thenReturn(dataHelper.createF061Resp("456662112", "+00090001232"));
        when(involvedPartyDetails.kycStatusEvaluator.isKycCompliant(productArrangement.getPrimaryInvolvedParty(), getProductHoldings(), dataHelper.createF061Resp("456662112", "+00090001232").getPartyEnqData())).thenReturn(true);
        when(involvedPartyDetails.switchClient.getGlobalSwitchValue("SW_STPLnsVrdTrns", "LTB", false)).thenReturn(true);
        when(involvedPartyDetails.productEvaluator.getBrandSpecificProducts(getProductHoldings(), "LTB", true)).thenReturn(getProductHoldings());
        when(involvedPartyDetails.productEvaluator.isVerdeProduct(getProductHoldings().get(0), "LTB", true)).thenReturn(true);
        involvedPartyDetails.identifyInvolvedPartyDetails(header, productArrangement);

        assertFalse(StringUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()));
        assertNotEquals("0", productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        assertEquals("2", productArrangement.getPrimaryInvolvedParty().getCustomerSegment());
        assertEquals("3214", productArrangement.getPrimaryInvolvedParty().getCbsCustomerNumber());
        assertFalse(productArrangement.getExistingProducts().isEmpty());
    }

    @Test
    public void testIdentifyInvolvedPartyWhenCustomerSegmentIsNotPresent() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("11221132");
        when(involvedPartyDetails.productHoldingRetriever.getProductHoldings(header, "11221132")).thenReturn(getProductHoldings());
        when(involvedPartyDetails.customerUtility.getCBSCustomerNumber(getProductHoldings())).thenReturn("3214");
        when(involvedPartyDetails.customerUtility.getCustomerSegment(getProductHoldings())).thenReturn("2");
        when(involvedPartyDetails.customerUtility.getFDICustomerID(getProductHoldings())).thenReturn("2201");
        when(involvedPartyDetails.involvedPartyRetriever.retrieveInvolvedPartyDetails(header, "11221132")).thenReturn(dataHelper.createF061Resp("456662112", "+00090001232"));
        when(involvedPartyDetails.kycStatusEvaluator.isKycCompliant(productArrangement.getPrimaryInvolvedParty(), getProductHoldings(), dataHelper.createF061Resp("456662112", "+00090001232").getPartyEnqData())).thenReturn(true);
        when(involvedPartyDetails.switchClient.getGlobalSwitchValue("SW_STPLnsVrdTrns", "LTB", false)).thenReturn(true);
        when(involvedPartyDetails.productEvaluator.getBrandSpecificProducts(getProductHoldings(), "LTB", true)).thenReturn(getProductHoldings());
        when(involvedPartyDetails.productEvaluator.isVerdeProduct(getProductHoldings().get(0), "LTB", true)).thenReturn(false);

        involvedPartyDetails.identifyInvolvedPartyDetails(header, productArrangement);

        assertFalse(StringUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()));
        assertNotEquals("0", productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        assertEquals("2", productArrangement.getPrimaryInvolvedParty().getCustomerSegment());
        assertEquals("3214", productArrangement.getPrimaryInvolvedParty().getCbsCustomerNumber());
        assertFalse(productArrangement.getExistingProducts().isEmpty());
    }

    @Test
    public void testIdentifyInvolvedPartyWhenCustomerIdentifierIsNotPresent() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        when(involvedPartyDetails.customerEvaluator.isBirthDate(header, productArrangement)).thenReturn(true);
        involvedPartyDetails.identifyInvolvedPartyDetails(header, productArrangement);

        assertNotEquals("0", productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        assertEquals("3", productArrangement.getPrimaryInvolvedParty().getCustomerSegment());
        assertTrue(productArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator());
        assertFalse(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().isIsStaffMember());
        assertFalse(productArrangement.getPrimaryInvolvedParty().isIsAuthCustomer());
    }

    @Test
    public void testIdentifyInvolvedPartyWhenCustomerEvaluatorReturnsFalse() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        when(involvedPartyDetails.customerEvaluator.isBirthDate(header, productArrangement)).thenReturn(false);
        involvedPartyDetails.identifyInvolvedPartyDetails(header, productArrangement);

        assertEquals("0", productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
    }

    private List<Product> getProductHoldings() {
        List<Product> productList = new ArrayList<>();
        ProductPartyData productPartyData = dataHelper.createF336Response().getProductPartyData().get(0);
        Product product = new Product();

        product.setProductIdentifier(String.valueOf(productPartyData.getProdGroupId()));
        product.setBrandName(productPartyData.getSellerLegalEntCd());
        product.setIPRTypeCode(productPartyData.getIPRTypeCd());
        product.setRoleCode(productPartyData.getProdHeldRoleCd());
        product.setStatusCode(productPartyData.getProdHeldStatusCd());
        DateFactory dateFactory = new DateFactory();
        product.setAmendmentEffectiveDate(dateFactory.stringToXMLGregorianCalendar(productPartyData.getAmdEffDt(), new SimpleDateFormat("ddMMyyyy")));

        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode(String.format("%05d", productPartyData.getExtSysId()));
        extSysProdIdentifier.setProductIdentifier(productPartyData.getExtProdIdTx());
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);

        ProductOffer productOffer = new ProductOffer();
        productOffer.setStartDate(dateFactory.stringToXMLGregorianCalendar(productPartyData.getProductHeldOpenDt(), new SimpleDateFormat("ddMMyyyy")));
        product.getProductoffer().add(productOffer);

        product.setProductName(productPartyData.getExtProductDs());
        product.setProductType(String.valueOf(productPartyData.getProdGroupId()));
        product.setExtPartyIdTx(productPartyData.getExtPartyIdTx());

        if (productPartyData.getExtProdHeldIdTx() != null && productPartyData.getExtProdHeldIdTx().length() == 19) {
            product.setExternalProductHeldIdentifier(productPartyData.getExtProdHeldIdTx());
        }
        productList.add(product);
        return productList;
    }

}
