package com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.convert.GenerateDocumentRequestFactory;
import com.lloydsbanking.salsa.downstream.cm.client.GenerateDocumentClient;
import lib_sim_bo.businessobjects.DocumentationItem;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentInternalServiceErrorMsg;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentResourceNotAvailableErrorMsg;
import lib_sim_communicationmanager.messages.GenerateDocumentRequest;
import lib_sim_communicationmanager.messages.GenerateDocumentResponse;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class GenerateDocumentRetrieverTest {
    GenerateDocumentRetriever generateDocumentRetriever;
    FinanceServiceArrangement financeServiceArrangement;
    RequestHeader requestHeader;
    ApplicationDetails applicationDetails;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        generateDocumentRetriever = new GenerateDocumentRetriever();
        testDataHelper = new TestDataHelper();
        generateDocumentRetriever.generateDocumentRequestFactory = mock(GenerateDocumentRequestFactory.class);
        generateDocumentRetriever.applicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
        generateDocumentRetriever.generateDocumentClient = mock(GenerateDocumentClient.class);
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        requestHeader = testDataHelper.createApaRequestHeader();
        applicationDetails = new ApplicationDetails();
        GenerateDocumentRequest generateDocumentRequest = new GenerateDocumentRequest();
        generateDocumentRequest.setDocumentationItem(new DocumentationItem());
        when(generateDocumentRetriever.generateDocumentRequestFactory.convert(any(FinanceServiceArrangement.class), any(RequestHeader.class), any(List.class))).thenReturn(generateDocumentRequest);
    }

    @Test
    public void testCallGenerateDocumentService() throws GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg {
        GenerateDocumentResponse generateDocumentResponse = new GenerateDocumentResponse();
        when(generateDocumentRetriever.generateDocumentClient.generateDocument(generateDocumentRetriever.generateDocumentRequestFactory.convert(financeServiceArrangement, requestHeader, financeServiceArrangement.getAssociatedProduct().getProductoffer()))).thenReturn(generateDocumentResponse);
        generateDocumentResponse = generateDocumentRetriever.callGenerateDocumentService(financeServiceArrangement, requestHeader, applicationDetails);
        assertNotNull(generateDocumentResponse);
    }

    @Test
    public void testCallGenerateDocumentServiceForException() throws GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg {
        when(generateDocumentRetriever.generateDocumentClient.generateDocument(generateDocumentRetriever.generateDocumentRequestFactory.convert(financeServiceArrangement, requestHeader, financeServiceArrangement.getAssociatedProduct().getProductoffer()))).thenThrow(WebServiceException.class);
        generateDocumentRetriever.callGenerateDocumentService(financeServiceArrangement, requestHeader, applicationDetails);
        verify(generateDocumentRetriever.applicationStatusHelper).setApplicationDetails(any(Integer.class), any(String.class), any(String.class), any(String.class), any(String.class), any(ApplicationDetails.class));
    }
}
