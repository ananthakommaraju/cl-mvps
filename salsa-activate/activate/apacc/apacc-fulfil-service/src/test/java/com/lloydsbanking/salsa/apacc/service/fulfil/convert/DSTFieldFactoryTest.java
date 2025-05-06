package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import lib_sbo_cardacquire.businessojects.Field;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class DSTFieldFactoryTest {

    DSTFieldFactory dstFieldFactory;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        dstFieldFactory = new DSTFieldFactory();
        testDataHelper = new TestDataHelper();
        dstFieldFactory.dstFieldHelper = new DSTFieldHelper();
        dstFieldFactory.dstIndividualFactory = mock(DSTIndividualFactory.class);
    }

    @Test
    public void getFieldListTest() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        List<Field> fieldList = dstFieldFactory.getFieldList(financeServiceArrangement);
        assertFalse(fieldList.isEmpty());
        assertEquals("UK", fieldList.get(0).getValue());
    }

    @Test
    public void getFieldListWithEmptyTelephoneAndPostalAddressListTest() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        financeServiceArrangement.getPrimaryInvolvedParty().getTelephoneNumber().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        List<Field> fieldList = dstFieldFactory.getFieldList(financeServiceArrangement);
        assertFalse(fieldList.isEmpty());
    }

    @Test
    public void getFieldListWithDirectDebitTest() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        DirectDebit directDebit = new DirectDebit();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal("100"));
        directDebit.setAmount(currencyAmount);
        financeServiceArrangement.setDirectDebit(directDebit);
        List<Field> fieldList = dstFieldFactory.getFieldList(financeServiceArrangement);
        assertFalse(fieldList.isEmpty());
        boolean flag = false;
        for (Field field : fieldList) {
            if ("DDSP".equals(field.getName()) && "100".equals(field.getValue())) {
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getFieldListWithProductAttributesMACAndISSTest() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        ProductAttributes attribute1 = new ProductAttributes();
        attribute1.setAttributeCode("MAC");
        attribute1.setAttributeValue("1234");
        ProductAttributes attribute2 = new ProductAttributes();
        attribute2.setAttributeCode("ISS_ID");
        attribute2.setAttributeValue("456");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().add(attribute1);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().add(attribute2);
        List<Field> fieldList = dstFieldFactory.getFieldList(financeServiceArrangement);
        assertFalse(fieldList.isEmpty());
        boolean flag = false;
        for (Field field : fieldList) {
            if ("MAC".equals(field.getName()) && "1234".equals(field.getValue())) {
                flag = true;
            } else if ("PCTD".equals(field.getName()) && "456".equals(field.getValue())) {
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getFieldListWithExternalSysProductIdTest() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("00107");
        extSysProdIdentifier.setProductIdentifier("12");
        financeServiceArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        List<Field> fieldList = dstFieldFactory.getFieldList(financeServiceArrangement);
        assertFalse(fieldList.isEmpty());
        boolean flag = false;
        for (Field field : fieldList) {
            if ("PROD".equals(field.getName()) && "0012".equals(field.getValue())) {
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getFieldListWithPreviousAddressTest() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStatusCode("PREVIOUS");
        List<Field> fieldList = dstFieldFactory.getFieldList(financeServiceArrangement);
        assertFalse(fieldList.isEmpty());
        boolean flag = false;
        for (Field field : fieldList) {
            if ("PPA1".equals(field.getName())) {
                flag = true;
            }
        }
        assertTrue(flag);
    }
}
