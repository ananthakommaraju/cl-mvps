package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData;
import com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ri.ContentType;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ri.InformationContent;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentResponse;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RecordDocumentMetaContentRequestBuilderTest {
    RecordDocumentMetaContentRequestBuilder recordDocumentMetaContentRequestBuilder;
    RetrieveDocumentMetaContentResponse retrieveDocumentMetaContentResponse;
    DateFactory dateFactory;
    Customer customer;
    DocumentContent documentContent;
    DocumentContent documentContent1;

    @Before
    public void setUp() throws DatatypeConfigurationException, ParseException {
        dateFactory = new DateFactory();
        customer = new Customer();
        Individual individual = new Individual();
        individual.setPlaceOfBirth("Place of Birth");
        customer.setIsPlayedBy(individual);
        recordDocumentMetaContentRequestBuilder = new RecordDocumentMetaContentRequestBuilder();
        retrieveDocumentMetaContentResponse = new RetrieveDocumentMetaContentResponse();
        documentContent = new DocumentContent();
        ContentType contentType = new ContentType();
        contentType.setDescription("Place of Birth");
        documentContent.setContentType(contentType);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss.SSS");
        String time = "10:14:28.999Z";
        Date date = dateFormat1.parse(time);
        XMLGregorianCalendar maintainanceDate = dateFactory.stringToXMLGregorianCalendar("2015-07-07", dateFormat);
        XMLGregorianCalendar maintainanceTime = dateFactory.dateToXMLGregorianCalendar(date);


        MaintenanceAuditElement maintenanceAuditElement = new MaintenanceAuditElement();
        maintenanceAuditElement.setMaintenanceAuditDate(maintainanceDate);
        maintenanceAuditElement.setMaintenanceAuditTime(maintainanceTime);
        List<MaintenanceAuditElement> maintenanceAuditElementList = new ArrayList<>();
        maintenanceAuditElementList.add(maintenanceAuditElement);
        MaintenanceAuditData maintenanceAuditData = new MaintenanceAuditData();
        maintenanceAuditData.getHasMaintenanceAuditElement().addAll(maintenanceAuditElementList);
        List<MaintenanceAuditData> maintenanceAuditDataList = new ArrayList<>();
        maintenanceAuditDataList.add(maintenanceAuditData);
        documentContent.getMaintenanceAuditData().addAll(maintenanceAuditDataList);
        List<InformationContent> informationContentList = new ArrayList<>();
        InformationContent informationContent = new DocumentContent();
        informationContent.setDescription("<![CDATA[<IFWXML_Argument:argument xmlns:IFWXML_Argument=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument\"\n" +
                "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\txsi:schemaLocation=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument LDMArgument.xsd\"><IFWXML_Argument:argumentData><IFWXML_Argument:effectiveDate>2015-08-28+01:00</IFWXML_Argument:effectiveDate><IFWXML_Argument:name>Place of Birth</IFWXML_Argument:name><IFWXML_Argument:identifier>220</IFWXML_Argument:identifier><IFWXML_Argument:sequenceNumber>1</IFWXML_Argument:sequenceNumber><IFWXML_Argument:value>Bhopal</IFWXML_Argument:value><IFWXML_Argument:argumentAudit><IFWXML_Argument:externalSystemID>19</IFWXML_Argument:externalSystemID><IFWXML_Argument:externalUserID>KS442736</IFWXML_Argument:externalUserID><IFWXML_Argument:externalLocationID>0000777505</IFWXML_Argument:externalLocationID><IFWXML_Argument:auditDate>2015-10-21</IFWXML_Argument:auditDate><IFWXML_Argument:auditTime>10:14:29.105Z</IFWXML_Argument:auditTime></IFWXML_Argument:argumentAudit></IFWXML_Argument:argumentData></IFWXML_Argument:argument>]]>");
        informationContentList.add(informationContent);
        documentContent.getIncludesContent().addAll(informationContentList);

        documentContent1 = new DocumentContent();
        ContentType contentType1 = new ContentType();
        contentType1.setDescription("Place of Birth");
        documentContent1.setContentType(contentType1);
        SimpleDateFormat newdateFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat newdateFormat1 = new SimpleDateFormat("HH:mm:ss.SSS");
        String newtime = "10:14:28.999Z";
        Date newdate = newdateFormat1.parse(newtime);
        XMLGregorianCalendar maintainanceDate1 = dateFactory.stringToXMLGregorianCalendar("2014-07-07", newdateFormat);
        XMLGregorianCalendar maintainanceTime1 = dateFactory.dateToXMLGregorianCalendar(newdate);
        MaintenanceAuditElement maintenanceAuditElement1 = new MaintenanceAuditElement();
        maintenanceAuditElement1.setMaintenanceAuditDate(maintainanceDate1);
        maintenanceAuditElement1.setMaintenanceAuditTime(maintainanceTime1);
        List<MaintenanceAuditElement> maintenanceAuditElementList1 = new ArrayList<>();
        maintenanceAuditElementList1.add(maintenanceAuditElement1);
        MaintenanceAuditData maintenanceAuditData1 = new MaintenanceAuditData();
        maintenanceAuditData1.getHasMaintenanceAuditElement().addAll(maintenanceAuditElementList1);
        List<MaintenanceAuditData> maintenanceAuditDataList1 = new ArrayList<>();
        maintenanceAuditDataList1.add(maintenanceAuditData1);
        documentContent1.getMaintenanceAuditData().addAll(maintenanceAuditDataList1);
        List<InformationContent> informationContentList1 = new ArrayList<>();
        InformationContent informationContent1 = new DocumentContent();
        informationContent1.setDescription("<![CDATA[<IFWXML_Argument:argument xmlns:IFWXML_Argument=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument\"\n" +
                "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\txsi:schemaLocation=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument LDMArgument.xsd\"><IFWXML_Argument:argumentData><IFWXML_Argument:effectiveDate>2015-08-28+01:00</IFWXML_Argument:effectiveDate><IFWXML_Argument:name>Place of Birth</IFWXML_Argument:name><IFWXML_Argument:identifier>220</IFWXML_Argument:identifier><IFWXML_Argument:sequenceNumber>1</IFWXML_Argument:sequenceNumber><IFWXML_Argument:value>Bhopal</IFWXML_Argument:value><IFWXML_Argument:argumentAudit><IFWXML_Argument:externalSystemID>19</IFWXML_Argument:externalSystemID><IFWXML_Argument:externalUserID>KS442736</IFWXML_Argument:externalUserID><IFWXML_Argument:externalLocationID>0000777505</IFWXML_Argument:externalLocationID><IFWXML_Argument:auditDate>2015-10-21</IFWXML_Argument:auditDate><IFWXML_Argument:auditTime>10:14:29.105Z</IFWXML_Argument:auditTime></IFWXML_Argument:argumentAudit></IFWXML_Argument:argumentData></IFWXML_Argument:argument>]]>");
        informationContentList1.add(informationContent1);
        documentContent1.getIncludesContent().addAll(informationContentList1);


        retrieveDocumentMetaContentResponse.getDocumentContent().add(documentContent);
        retrieveDocumentMetaContentResponse.getDocumentContent().add(documentContent1);
        recordDocumentMetaContentRequestBuilder.dateFactory = new DateFactory();
        recordDocumentMetaContentRequestBuilder.descriptionFromCDATABuilder = mock(DocumentBuilder.class);
    }

    @Test
    public void testConvert() {
        when(recordDocumentMetaContentRequestBuilder.descriptionFromCDATABuilder.getDescriptionFromCDATA(anyString(), anyString())).thenReturn("xml");
        RecordDocumentMetaContentRequest recordDocumentMetaContentRequest = recordDocumentMetaContentRequestBuilder.convert(customer, retrieveDocumentMetaContentResponse);
        assertNotNull(recordDocumentMetaContentRequest);
        assertEquals("xml", recordDocumentMetaContentRequest.getDocumentContent().get(0).getIncludesContent().get(0).getDescription());
        assertEquals("xml", recordDocumentMetaContentRequest.getDocumentContent().get(1).getIncludesContent().get(0).getDescription());
    }

    @Test
    public void testConvertContentTypeNull() {
        when(recordDocumentMetaContentRequestBuilder.descriptionFromCDATABuilder.getDescriptionFromCDATA(anyString(), anyString())).thenReturn("xml");
        retrieveDocumentMetaContentResponse.getDocumentContent().get(0).setContentType(null);
        RecordDocumentMetaContentRequest recordDocumentMetaContentRequest = recordDocumentMetaContentRequestBuilder.convert(customer, retrieveDocumentMetaContentResponse);
        assertNotNull(recordDocumentMetaContentRequest);
        assertEquals("xml", recordDocumentMetaContentRequest.getDocumentContent().get(0).getIncludesContent().get(0).getDescription());
        assertEquals("xml", recordDocumentMetaContentRequest.getDocumentContent().get(1).getIncludesContent().get(0).getDescription());
    }

    @Test
    public void testConvertgetDocumentContentClear() {
        when(recordDocumentMetaContentRequestBuilder.descriptionFromCDATABuilder.getDescriptionFromCDATA(anyString(), anyString())).thenReturn("xml");
        retrieveDocumentMetaContentResponse.getDocumentContent().clear();
        RecordDocumentMetaContentRequest recordDocumentMetaContentRequest = recordDocumentMetaContentRequestBuilder.convert(customer, retrieveDocumentMetaContentResponse);
    }
}
