package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.EmployersAddrUpdDataType;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class EmployerAddressDataFactoryTest {
    @Test
    public void testGenerateEmployerAddress() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("addressline1");
        unstructuredAddress.setAddressLine2("addressline2");
        unstructuredAddress.setAddressLine3("addressline3");
        unstructuredAddress.setAddressLine4("addressline4");
        unstructuredAddress.setAddressLine5("addressline5");
        unstructuredAddress.setAddressLine6("addressline6");
        unstructuredAddress.setAddressLine7("addressline7");
        unstructuredAddress.setPostCode("postcode");

        EmployerAddressDataFactory employerAddressDataFactory = new EmployerAddressDataFactory();
        EmployersAddrUpdDataType employersAddrUpdDataType = employerAddressDataFactory.generateEmployerAddress(unstructuredAddress);
        assertEquals("addressline1", employersAddrUpdDataType.getAddressLine1Tx());
        assertEquals("addressline2", employersAddrUpdDataType.getAddressLine2Tx());
        assertEquals("addressline3", employersAddrUpdDataType.getAddressLine3Tx());
        assertEquals("addressline4", employersAddrUpdDataType.getAddressLine4Tx());
        assertEquals("addressline5", employersAddrUpdDataType.getAddressLine5Tx());
        assertEquals("addressline6", employersAddrUpdDataType.getAddressLine6Tx());
        assertEquals("addressline7", employersAddrUpdDataType.getAddressLine7Tx());
        assertEquals("postcode", employersAddrUpdDataType.getPostCd());
    }
}
