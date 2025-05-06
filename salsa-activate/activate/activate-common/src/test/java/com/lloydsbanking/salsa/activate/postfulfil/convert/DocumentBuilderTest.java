package com.lloydsbanking.salsa.activate.postfulfil.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class DocumentBuilderTest {
    DocumentBuilder documentBuilder;

    @Before
    public void setUp() {
        documentBuilder = new DocumentBuilder();
        documentBuilder.dateFactory = new DateFactory();
    }

    @Test
    public void testGetDescriptionFromCDATA() {
        String xml = "<![CDATA[<IFWXML_Argument:argument xmlns:IFWXML_Argument=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument\"\n" +
                "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\txsi:schemaLocation=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument LDMArgument.xsd\"><IFWXML_Argument:argumentData><IFWXML_Argument:effectiveDate>2015-08-28+01:00</IFWXML_Argument:effectiveDate><IFWXML_Argument:name>Place of Birth</IFWXML_Argument:name><IFWXML_Argument:identifier>220</IFWXML_Argument:identifier><IFWXML_Argument:sequenceNumber>1</IFWXML_Argument:sequenceNumber><IFWXML_Argument:value>Bhopal</IFWXML_Argument:value><IFWXML_Argument:argumentAudit><IFWXML_Argument:externalSystemID>19</IFWXML_Argument:externalSystemID><IFWXML_Argument:externalUserID>KS442736</IFWXML_Argument:externalUserID><IFWXML_Argument:externalLocationID>0000777505</IFWXML_Argument:externalLocationID><IFWXML_Argument:auditDate>2015-10-21</IFWXML_Argument:auditDate><IFWXML_Argument:auditTime>10:14:29.105Z</IFWXML_Argument:auditTime></IFWXML_Argument:argumentAudit></IFWXML_Argument:argumentData></IFWXML_Argument:argument>]]>";

        String updatedxml = documentBuilder.getDescriptionFromCDATA(xml, "Place Of Birth");
        assertNotNull(updatedxml);
    }

    @Test
    public void testGetDescriptionFromCDATAWhenXmlIsNull() {

        String updatedxml = documentBuilder.getDescriptionFromCDATA(null, "Place Of Birth");
        assertNotNull(updatedxml);
    }
}
