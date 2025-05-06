package com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.convert.InformationContentFactory;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Template;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_communicationmanager.messages.GenerateDocumentRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class GenerateDocumentRequestFactoryTest {
    GenerateDocumentRequestFactory generateDocumentRequestFactory;
    FinanceServiceArrangement financeServiceArrangement;
    TestDataHelper testDataHelper;
    GenerateDocumentRequest request;
    RequestHeader requestHeader;

    @Before
    public void setUp() {
        generateDocumentRequestFactory = new GenerateDocumentRequestFactory();
        testDataHelper = new TestDataHelper();
        request = new GenerateDocumentRequest();
        generateDocumentRequestFactory.informationContentFactory = mock(InformationContentFactory.class);
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        requestHeader = testDataHelper.createApaRequestHeader();
        financeServiceArrangement.getAffiliatedetails().get(0).setAffliateAddress(new UnstructuredAddress());
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine1("abc");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine2("ab");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine3("a");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine4("ab");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine5("abe");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine6("a");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setAddressLine7("abc");
        financeServiceArrangement.getAffiliatedetails().get(0).getAffliateAddress().setPostCode("123");
    }

    @Test
    public void testConvert() {
        Template template = new Template();
        template.setExternalTemplateIdentifier("CCA_");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getTemplate().add(template);
        request = generateDocumentRequestFactory.convert(financeServiceArrangement, requestHeader, financeServiceArrangement.getAssociatedProduct().getProductoffer());
        assertEquals("PDF", request.getDocumentationItem().getFormat().toString());
        assertEquals("CCA__SGND", request.getDocumentationItem().getHasContent().getContentTemplateId());
    }

    @Test
    public void testConvertWhenAgreementDateIsNull() {
        Template template = new Template();
        template.setExternalTemplateIdentifier("CCA_");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getTemplate().add(template);
        financeServiceArrangement.setAgreementAcceptedDate(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setIsPAFFormat(false);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        request = generateDocumentRequestFactory.convert(financeServiceArrangement, requestHeader, financeServiceArrangement.getAssociatedProduct().getProductoffer());
        assertEquals("PDF", request.getDocumentationItem().getFormat().toString());
        assertEquals("CCA__SGND", request.getDocumentationItem().getHasContent().getContentTemplateId());
    }

    @Test
    public void testConvertWhenIsAuthCustomerNotNull() {
        Template template = new Template();
        template.setExternalTemplateIdentifier("CCA_");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getTemplate().add(template);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setIsPAFFormat(false);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        request = generateDocumentRequestFactory.convert(financeServiceArrangement, requestHeader, financeServiceArrangement.getAssociatedProduct().getProductoffer());
        assertEquals("PDF", request.getDocumentationItem().getFormat().toString());
        assertEquals("CCA__SGND", request.getDocumentationItem().getHasContent().getContentTemplateId());
    }
}
