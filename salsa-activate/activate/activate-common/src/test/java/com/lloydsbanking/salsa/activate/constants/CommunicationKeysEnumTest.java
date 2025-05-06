package com.lloydsbanking.salsa.activate.constants;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class CommunicationKeysEnumTest {

    @Test
    public void testSendCommsKeysEnum(){
        assertEquals("IB.Product.CC.AppRefNum", CommunicationKeysEnum.ARRANGEMENT_ID.getKey());
        assertEquals("IB.Customer.FirstName", CommunicationKeysEnum.CUSTOMER_FIRSTNAME.getKey());
        assertEquals("IB.Product.ExpiryDate", CommunicationKeysEnum.FA_EXPIRY_DATE.getKey());
    }
}
