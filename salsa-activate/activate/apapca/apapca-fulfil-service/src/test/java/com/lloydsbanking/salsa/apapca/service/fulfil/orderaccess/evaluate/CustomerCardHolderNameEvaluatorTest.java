package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.CardholderNew;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.PlasticType;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class CustomerCardHolderNameEvaluatorTest {
    private CustomerCardHolderNameEvaluator customerCardHolderName;

    private TestDataHelper testDataHelper;

    private CardholderNew cardholderNew;

    private PlasticType plasticType;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        customerCardHolderName = new CustomerCardHolderNameEvaluator();
        cardholderNew = testDataHelper.createCardholderNew();
        plasticType = testDataHelper.createC846Response().getPlasticTypes().getPlasticType().get(0);
    }

    @Test
    public void testGetCardHolderName() {
       String name = customerCardHolderName.getCardHolderName(cardholderNew, plasticType);
       assertEquals("FGHI/ABCDEFHI.MR",name);

    }
    @Test
    public void testGetCardHolderNameWithCase2() {
        plasticType.setMaxCardholderNmLh(10);
        assertEquals("FGHI/A.MR",customerCardHolderName.getCardHolderName(cardholderNew, plasticType));

    }
    @Test
    public void testGetCardHolderNameWithCase3() {
        cardholderNew.getInitials().setSecondIt("B");
        plasticType.setMaxCardholderNmLh(8);
        assertEquals("FGHI/A B",customerCardHolderName.getCardHolderName(cardholderNew, plasticType));

    }
    @Test
    public void testGetCardHolderNameWithCase4(){
        cardholderNew.getInitials().setSecondIt("B");
        plasticType.setMaxCardholderNmLh(6);
        assertEquals("FGHI/A",customerCardHolderName.getCardHolderName(cardholderNew, plasticType));
    }
    @Test
    public void testGetCardHolderNameWithCase5(){
        cardholderNew.getInitials().setSecondIt("B");
        plasticType.setMaxCardholderNmLh(5);
        assertEquals("FGHI/",customerCardHolderName.getCardHolderName(cardholderNew, plasticType));
    }
    @Test
    public void testGetCardHolderNameWithCase6(){
        cardholderNew.getInitials().setSecondIt("B");
        plasticType.setMaxCardholderNmLh(4);
        assertEquals("FGH/",customerCardHolderName.getCardHolderName(cardholderNew, plasticType));
    }
    @Test
    public void testGetCardHolderNameWhenFirstNameIsNull(){
        cardholderNew.setFirstForeNm("");
        plasticType.setMaxCardholderNmLh(10);
        assertEquals("FGHI/A.MR",customerCardHolderName.getCardHolderName(cardholderNew, plasticType));
    }
}
