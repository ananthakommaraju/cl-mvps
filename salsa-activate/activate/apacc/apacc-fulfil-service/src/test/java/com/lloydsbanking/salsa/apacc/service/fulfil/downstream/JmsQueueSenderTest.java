package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.DSTRequestFactory;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sbo_cardacquire.businessojects.AWD;
import lib_sbo_cardacquire.businessojects.Transaction;
import lib_sbo_cardacquire.interfaces.cardacquiremqservice.DST;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class JmsQueueSenderTest {

    private JmsQueueSender jmsQueueSender;

    @Before
    public void setUp() {
        jmsQueueSender = new JmsQueueSender();
        jmsQueueSender.jmsTemplate=mock(JmsTemplate.class);
        jmsQueueSender.dstRequestFactory = mock(DSTRequestFactory.class);
        jmsQueueSender.applicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
        jmsQueueSender.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(jmsQueueSender.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void SendMessageWithCCA() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        byte[] image = null;
        DST dst = createDST("CCA");
        when(jmsQueueSender.dstRequestFactory.convert(financeServiceArrangement, "LTB", image)).thenReturn(dst);
        jmsQueueSender.send(financeServiceArrangement, "LTB", image, new ApplicationDetails());
    }

    @Test
    public void SendMessageWithoutCCA() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        byte[] image = null;
        DST dst = createDST(null);
        when(jmsQueueSender.dstRequestFactory.convert(financeServiceArrangement, "LTB", image)).thenReturn(dst);
        jmsQueueSender.send(financeServiceArrangement, "LTB", image, new ApplicationDetails());
    }

    private DST createDST(String type) {
        DST dst = new DST();
        dst.setJobName("GxyAWDCreate");
        dst.setReadable("Y");
        dst.setAWD(new AWD());
        dst.getAWD().setHost("AWD");
        dst.getAWD().setUserid("DSTSETUP");
        dst.getAWD().setTransaction(new Transaction());
        dst.getAWD().getTransaction().setSourceType(type);
        return dst;
    }

}
