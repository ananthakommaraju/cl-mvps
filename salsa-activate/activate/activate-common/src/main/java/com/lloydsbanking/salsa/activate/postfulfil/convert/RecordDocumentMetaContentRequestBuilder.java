package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData;
import com.lloydsbanking.salsa.soap.soa.dm.esb.common.ObjectReference;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.RequestHeader;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ip.Individual;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ip.InvolvedPartyObjectReference;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ip.InvolvedPartyRole;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ip.InvolvedPartyType;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ri.*;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentResponse;
import lib_sim_bo.businessobjects.Customer;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RecordDocumentMetaContentRequestBuilder {
    public static final String DATA_SOURCE_NAME = "133";
    public static final String PLACE_OF_BIRTH = "Place of Birth";
    public static final String CONTENT_NAME = "100";
    public static final String DOC_CONTENT_VERSION = "1";
    public static final String INVOLVED_PARTY_DESC = "Source";
    public static final String INVOLVED_PARTY_NAME = "1";
    private static final Logger LOGGER = Logger.getLogger(RecordDocumentMetaContentRequestBuilder.class);
    @Autowired
    DocumentBuilder descriptionFromCDATABuilder;
    @Autowired
    DateFactory dateFactory;

    public RecordDocumentMetaContentRequest convert(Customer customer, RetrieveDocumentMetaContentResponse retrieveDocumentMetaContentResponse) {
        RecordDocumentMetaContentRequest recordDocumentMetaContentRequest = new RecordDocumentMetaContentRequest();
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setDatasourceName(DATA_SOURCE_NAME);
        recordDocumentMetaContentRequest.setRequestHeader(requestHeader);
        List<String> dateTimeList = new ArrayList<>();
        for (DocumentContent documentContent : retrieveDocumentMetaContentResponse.getDocumentContent()) {
            if (null != documentContent.getContentType() && PLACE_OF_BIRTH.equalsIgnoreCase(documentContent.getContentType().getDescription())) {
                if (!CollectionUtils.isEmpty(documentContent.getMaintenanceAuditData())) {
                    String auditDateTime = getAuditDateTime(documentContent.getMaintenanceAuditData().get(0));
                    if (auditDateTime != null) {
                        dateTimeList.add(auditDateTime);
                    }
                }
            }
        }
        String latestDate = getLatestDate(dateTimeList);
        for (DocumentContent documentContent : retrieveDocumentMetaContentResponse.getDocumentContent()) {
            if (!CollectionUtils.isEmpty(documentContent.getMaintenanceAuditData())) {
                if (null != latestDate && latestDate.equalsIgnoreCase(getAuditDateTime(documentContent.getMaintenanceAuditData().get(0)))) {
                    recordDocumentMetaContentRequest.getDocumentContent().add(getDocumentContent(customer, documentContent));
                } else {
                    recordDocumentMetaContentRequest.getDocumentContent().add(getDocumentContent(customer, null));
                }
            }
        }
        return recordDocumentMetaContentRequest;
    }

    private String getAuditDateTime(MaintenanceAuditData maintenanceAuditData) {
        if (maintenanceAuditData != null && !CollectionUtils.isEmpty(maintenanceAuditData.getHasMaintenanceAuditElement())) {
            String auditDate = dateFactory.convertXMLGregorianToStringDateFormat(maintenanceAuditData.getHasMaintenanceAuditElement().get(0).getMaintenanceAuditDate(), "yyyy-mm-dd");
            String auditTime = dateFactory.convertXMLGregorianToStringDateFormat(maintenanceAuditData.getHasMaintenanceAuditElement().get(0).getMaintenanceAuditTime(), "HH:mm:ss.SSS");
            return (auditDate + " " + auditTime);
        }
        return null;
    }

    private DocumentContent getDocumentContent(Customer customer, DocumentContent documentContent) {
        LOGGER.info("Getting documentContent with customer | documentContent: " + customer + " | " + documentContent);
        DocumentContent documentContentNew = new DocumentContent();
        if (documentContent != null) {
            documentContentNew.getMaintenanceAuditData().addAll(documentContent.getMaintenanceAuditData());
            if (getContentType(documentContent.getContentType()) != null) {
                documentContentNew.setContentType(getContentType(documentContent.getContentType()));
            }
            if (documentContent.getVersion() != null) {
                documentContentNew.setVersion(documentContent.getVersion());
            }
            documentContentNew.setActedOnBy(getInvolvedPartyRole(documentContent));
            List<InformationContent> informationContents = getInformationContent(customer.getIsPlayedBy().getPlaceOfBirth(), documentContent.getIncludesContent());
            documentContentNew.getIncludesContent().addAll(informationContents);
        } else {
            ContentType defaultContentType = new ContentType();
            defaultContentType.setDescription(PLACE_OF_BIRTH);
            defaultContentType.setName(CONTENT_NAME);
            documentContentNew.setContentType(defaultContentType);
            documentContentNew.setVersion(DOC_CONTENT_VERSION);
            documentContentNew.setActedOnBy(getDefaultInvolvedParty(customer.getCustomerIdentifier()));
            DocumentContent documentContentDesc = new DocumentContent();
            documentContentDesc.setDescription(descriptionFromCDATABuilder.getDescriptionFromCDATA(null, customer.getIsPlayedBy().getPlaceOfBirth()));
            List<DocumentContent> informationContentList = new ArrayList<>();
            informationContentList.add(documentContentDesc);
            documentContentNew.getIncludesContent().addAll(informationContentList);
        }
        documentContentNew.setDocument(getDocumentationItem());
        return documentContentNew;
    }

    private ContentType getContentType(ContentType contentType) {
        ContentType contentTypeUpdated = null;
        if (contentType != null && contentType.getDescription() != null && contentType.getName() != null) {
            contentTypeUpdated = new ContentType();
            contentTypeUpdated.setDescription(contentType.getDescription());
            contentTypeUpdated.setName(contentType.getName());
        }
        return contentTypeUpdated;
    }

    private InvolvedPartyRole getInvolvedPartyRole(DocumentContent documentContent) {
        Individual individual = new Individual();
        InvolvedPartyType involvedPartyType = new InvolvedPartyType();
        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        if (documentContent.getObjectReference() != null && documentContent.getObjectReference().getIdentifier() != null) {
            InvolvedPartyObjectReference objectReference = new InvolvedPartyObjectReference();
            objectReference.setIdentifier(documentContent.getObjectReference().getIdentifier());
            individual.setObjectReference(objectReference);
        }
        if (documentContent.getActedOnBy() != null && !CollectionUtils.isEmpty(documentContent.getActedOnBy().getIsPlayedByParty())
                && null != documentContent.getActedOnBy().getIsPlayedByParty().get(0) && !CollectionUtils.isEmpty(documentContent.getActedOnBy().getIsPlayedByParty().get(0).getAssociatedInvolvedParties())) {
            if (documentContent.getActedOnBy().getIsPlayedByParty().get(0).getAssociatedInvolvedParties().get(0) != null && documentContent.getActedOnBy().getIsPlayedByParty().get(0).getAssociatedInvolvedParties().get(0).getHasPartyType() != null) {
                involvedPartyType.setDescription(documentContent.getActedOnBy().getIsPlayedByParty().get(0).getAssociatedInvolvedParties().get(0).getHasPartyType().getDescription());
                involvedPartyType.setName(documentContent.getActedOnBy().getIsPlayedByParty().get(0).getAssociatedInvolvedParties().get(0).getHasPartyType().getName());
            }
        }
        individual.setHasPartyType(involvedPartyType);
        Individual individualUpper = new Individual();
        individualUpper.getAssociatedInvolvedParties().add(individual);
        involvedPartyRole.getIsPlayedByParty().add(individualUpper);
        return involvedPartyRole;
    }
    private InvolvedPartyRole getDefaultInvolvedParty(String customerId) {
        Individual individual = new Individual();
        ObjectReference objectReference = new ObjectReference();
        if (!StringUtils.isEmpty(customerId)) {
            objectReference.setIdentifier(customerId);
        }
        individual.setObjectReference(objectReference);
        InvolvedPartyType involvedPartyType = new InvolvedPartyType();
        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        involvedPartyType.setDescription(INVOLVED_PARTY_DESC);
        involvedPartyType.setName(INVOLVED_PARTY_NAME);
        individual.setHasPartyType(involvedPartyType);
        Individual individualUpper = new Individual();
        individualUpper.getAssociatedInvolvedParties().add(individual);
        involvedPartyRole.getIsPlayedByParty().add(individualUpper);
        return involvedPartyRole;
    }
    private List<InformationContent> getInformationContent(String placeOfBirth, List<InformationContent> informationContents) {
        List<InformationContent> infoContents = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationContents) && null != informationContents.get(0) && !StringUtils.isEmpty(informationContents.get(0).getDescription())) {
            InformationContent infoContent = new DocumentContent();
            infoContent.setDescription(descriptionFromCDATABuilder.getDescriptionFromCDATA(informationContents.get(0).getDescription(), placeOfBirth));
            infoContents.add(infoContent);
        }
        return infoContents;
    }
    private DocumentationItem getDocumentationItem() {
        DocumentRegistration registration = new DocumentRegistration();
        try {
            registration.setStartTime(dateFactory.dateToXMLGregorianCalendar(new Date()));
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("DatatypeConfigurationException occurred ", e);
        }
        DocumentationItem documentationItem = new DocumentationItem();
        documentationItem.setDocuments(registration);
        return documentationItem;
    }

    private String getLatestDate(List<String> dateTimeList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");
        String latestDateInString = null;
        Date latestDateInDateFormat = null;
        for (String dateTime : dateTimeList) {
            try {
                if (!StringUtils.isEmpty(dateTime)) {
                    Date newDate = dateFormat.parse(dateTime);
                    if (latestDateInDateFormat == null || newDate.compareTo(latestDateInDateFormat) > 0) {
                        latestDateInString = dateTime;
                        latestDateInDateFormat = newDate;
                    }
                }
            } catch (ParseException e) {
                LOGGER.info("ParseException occurred ", e);
            }
        }
        return latestDateInString;
    }
}