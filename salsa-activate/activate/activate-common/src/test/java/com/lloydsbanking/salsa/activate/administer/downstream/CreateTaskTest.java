package com.lloydsbanking.salsa.activate.administer.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.administer.convert.X741RequestFactory;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.downstream.tms.client.x741.X741Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateTaskTest {
    CreateTask createTask;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    RequestHeader header;
    TaskCreationResponse x741Resp;


    @Before
    public void setUp() {
        createTask = new CreateTask();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createApaRequestByDBEvent().getProductArrangement();
        header = testDataHelper.createApaRequestHeader();
        x741Resp = testDataHelper.createTaskCreationResponse();
        createTask.x741RequestFactory = new X741RequestFactory();
        createTask.headerRetriever = new HeaderRetriever();
        createTask.x741Client = mock(X741Client.class);
        createTask.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(createTask.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngment");


    }

    @Test
    public void testTaskCreation() throws ActivateProductArrangementInternalSystemErrorMsg {
        when(createTask.x741Client.x741(any(TaskCreation.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(x741Resp);
        assertEquals("45", createTask.taskCreation(productArrangement, header, new ApplicationDetails()));
    }

    @Test
    public void testTaskCreationWithInternalServiceErrorWithSomeSeverityCode() throws ActivateProductArrangementInternalSystemErrorMsg {
        x741Resp.getCreateTaskReturn().getResultCondition().setSeverityCode((byte) 7);
        when(createTask.x741Client.x741(any(TaskCreation.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(x741Resp);
        assertEquals("45", createTask.taskCreation(productArrangement, header, new ApplicationDetails()));
    }

    @Test
    public void testTaskCreationWithInternalServiceErrorWithInternalServiceErrorWithNullTaskId() throws ActivateProductArrangementInternalSystemErrorMsg {
        x741Resp.getCreateTaskReturn().getTaskRoutingInformation().setTaskId(0);
        when(createTask.x741Client.x741(any(TaskCreation.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(x741Resp);
        assertEquals("0", createTask.taskCreation(productArrangement, header, new ApplicationDetails()));
    }



}
