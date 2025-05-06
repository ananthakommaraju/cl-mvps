package com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.convert;

import com.lloydsbanking.salsa.activate.communication.convert.InformationContentFactory;
import com.lloydsbanking.salsa.activate.constants.CommunicationKeysEnum;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.messages.GenerateDocumentRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class GenerateDocumentRequestFactory {

    private static final String TEMPLATE_PREFIX_CCA = "CCA_";
    private static final String TEMPLATE_SUFFIX_SGND = "_SGND";
    private static final String ADDRESS_TYPE_CURRENT = "CURRENT";
    private static final String FILE_TYPE_PDF = "PDF";
    private static final int ADDRESS_LINE_COUNT_3 = 3;
    @Autowired
    InformationContentFactory informationContentFactory;

    public GenerateDocumentRequest convert(FinanceServiceArrangement financeServiceArrangement, RequestHeader header, List<ProductOffer> productOfferList) {
        GenerateDocumentRequest request = new GenerateDocumentRequest();
        request.setHeader(header);
        DocumentationItem documentationItem = new DocumentationItem();
        if (productOfferList != null) {
            documentationItem.setFormat(DocumentFormat.valueOf(FILE_TYPE_PDF));
            DocumentationContent documentationContent = new DocumentationContent();
            String contentTemplateId = getContentTemplateId(productOfferList.get(0).getTemplate());
            if (contentTemplateId != null) {
                documentationContent.setContentTemplateId(contentTemplateId);
            }
            documentationContent.getHasSpecifiedContent().addAll(getInformationContentList(financeServiceArrangement, productOfferList));
            documentationItem.setHasContent(documentationContent);
        }
        request.setDocumentationItem(documentationItem);
        return request;
    }

    private String getContentTemplateId(List<Template> templateList) {
        String contentTemplateId = null;
        for (Template template : templateList) {
            if (template.getExternalTemplateIdentifier().startsWith(TEMPLATE_PREFIX_CCA)) {
                contentTemplateId = template.getExternalTemplateIdentifier() + TEMPLATE_SUFFIX_SGND;
                break;
            }
        }
        return contentTemplateId;
    }

    public List<InformationContent> getInformationContentList(FinanceServiceArrangement financeServiceArrangement, List<ProductOffer> productOfferList) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null) {
            informationContentList.addAll(getCustomerNameInformationContentList(financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName()));
        }
        informationContentList.addAll(getAddressInformationContentList(financeServiceArrangement));
        informationContentList.addAll(getAffiliateDetailsInformationContentList(financeServiceArrangement));

        String agreementDate;
        if (financeServiceArrangement.getAgreementAcceptedDate() != null) {
            agreementDate = FastDateFormat.getInstance("dd/MM/yyyy").format(financeServiceArrangement.getAgreementAcceptedDate().toGregorianCalendar().getTime());
        } else {
            agreementDate = FastDateFormat.getInstance("dd/MM/yyyy").format(new Date());
        }
        informationContentFactory.setInformationContent(CommunicationKeysEnum.CCA_SIGNED_DATE.getKey(), agreementDate, informationContentList);

        if (financeServiceArrangement.getAssociatedProduct() != null) {
            informationContentFactory.setInformationContent(CommunicationKeysEnum.CC_PRODUCT_NAME.getKey(), financeServiceArrangement.getAssociatedProduct().getProductName(), informationContentList);
            if (!CollectionUtils.isEmpty(productOfferList)) {
                for (ProductAttributes productAttributes : productOfferList.get(0).getProductattributes()) {
                    informationContentFactory.setInformationContent(getKeyName(productAttributes.getAttributeCode()), productAttributes.getAttributeValue(), informationContentList);
                }
            }
        }

        return informationContentList;
    }

    private List<InformationContent> getAffiliateDetailsInformationContentList(FinanceServiceArrangement financeServiceArrangement) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(financeServiceArrangement.getAffiliatedetails())) {
            AffiliateDetails affiliatedetails = financeServiceArrangement.getAffiliatedetails().get(0);
            if (affiliatedetails != null) {
                informationContentFactory.setInformationContent(CommunicationKeysEnum.INTERMEDIARY_NAME.getKey(), affiliatedetails.getAffiliateDescription(), informationContentList);
                UnstructuredAddress affiliateAddress = affiliatedetails.getAffliateAddress();
                if (affiliateAddress != null) {
                    informationContentFactory.setInformationContent(CommunicationKeysEnum.INTERMEDIARY_ADDRESS_LINE_1.getKey(), affiliateAddress.getAddressLine1(), informationContentList);
                    informationContentFactory.setInformationContent(CommunicationKeysEnum.INTERMEDIARY_ADDRESS_LINE_2.getKey(), affiliateAddress.getAddressLine2(), informationContentList);
                    String addressLine3 = (affiliateAddress.getAddressLine3() + affiliateAddress.getAddressLine4() + affiliateAddress.getAddressLine5() + affiliateAddress.getAddressLine6() + affiliateAddress.getAddressLine7() + affiliateAddress.getPostCode()).replaceAll("null,", "").replaceAll(",$", "");
                    informationContentFactory.setInformationContent(CommunicationKeysEnum.INTERMEDIARY_ADDRESS_LINE_3.getKey(), addressLine3, informationContentList);
                }
            }
        }
        return informationContentList;
    }

    private List<InformationContent> getCustomerNameInformationContentList(List<IndividualName> individualName) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(individualName)) {
            informationContentFactory.setInformationContent(CommunicationKeysEnum.CUSTOMER_TITLE.getKey(), individualName.get(0).getPrefixTitle(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.CUSTOMER_FIRSTNAME.getKey(), individualName.get(0).getFirstName(), informationContentList);
            if (!CollectionUtils.isEmpty(individualName.get(0).getMiddleNames())) {
                informationContentFactory.setInformationContent(CommunicationKeysEnum.CUSTOMER_MIDDLENAMES.getKey(), individualName.get(0).getMiddleNames().get(0), informationContentList);
            }
            informationContentFactory.setInformationContent(CommunicationKeysEnum.CUSTOMER_LASTNAME.getKey(), individualName.get(0).getLastName(), informationContentList);
        }
        return informationContentList;
    }

    private List<InformationContent> getAddressInformationContentList(FinanceServiceArrangement financeServiceArrangement) {
        List<InformationContent> informationContentList = new ArrayList<>();
        for (PostalAddress postalAddress : financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress()) {
            if (ADDRESS_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                if (postalAddress.isIsPAFFormat() != null && postalAddress.isIsPAFFormat() && postalAddress.getStructuredAddress() != null) {
                    informationContentList.addAll(getStructuredAddressInformationContentList(postalAddress.getStructuredAddress()));
                } else if (postalAddress.getUnstructuredAddress() != null) {
                    informationContentList.addAll(getUnstructuredAddressList(financeServiceArrangement.getPrimaryInvolvedParty().isIsAuthCustomer(), postalAddress.getUnstructuredAddress()));
                }
                break;
            }
        }
        return informationContentList;
    }

    private List<InformationContent> getUnstructuredAddressList(Boolean isAuthCustomer, UnstructuredAddress unstructuredAddress) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (isAuthCustomer != null && isAuthCustomer) {
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_1.getKey(), unstructuredAddress.getAddressLine1(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_2.getKey(), unstructuredAddress.getAddressLine2(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_3.getKey(), unstructuredAddress.getAddressLine3(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_4.getKey(), unstructuredAddress.getAddressLine4(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_5.getKey(), unstructuredAddress.getAddressLine5(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_6.getKey(), unstructuredAddress.getAddressLine6(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE_7.getKey(), unstructuredAddress.getAddressLine7(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.PRODUCT_POSTCODE.getKey(), unstructuredAddress.getPostCode(), informationContentList);
        } else {
            informationContentFactory.setInformationContent(CommunicationKeysEnum.BUILDING_NUMBER.getKey(), unstructuredAddress.getAddressLine1(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.BUILDING_NAME.getKey(), unstructuredAddress.getAddressLine2(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.SUB_BUILDING_NAME.getKey(), unstructuredAddress.getAddressLine3(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.STREET.getKey(), unstructuredAddress.getAddressLine4(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.DISTRICT.getKey(), unstructuredAddress.getAddressLine5(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.CITY.getKey(), unstructuredAddress.getAddressLine6(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.COUNTRY.getKey(), unstructuredAddress.getAddressLine7(), informationContentList);
            informationContentFactory.setInformationContent(CommunicationKeysEnum.PRODUCT_POSTCODE.getKey(), unstructuredAddress.getPostCode(), informationContentList);
        }
        return informationContentList;
    }

    private List<InformationContent> getStructuredAddressInformationContentList(StructuredAddress structuredAddress) {
        List<InformationContent> informationContentList = new ArrayList<>();
        informationContentFactory.setInformationContent(CommunicationKeysEnum.BUILDING_NUMBER.getKey(), structuredAddress.getBuildingNumber(), informationContentList);
        informationContentFactory.setInformationContent(CommunicationKeysEnum.BUILDING_NAME.getKey(), structuredAddress.getBuilding(), informationContentList);
        informationContentFactory.setInformationContent(CommunicationKeysEnum.SUB_BUILDING_NAME.getKey(), structuredAddress.getSubBuilding(), informationContentList);
        informationContentFactory.setInformationContent(CommunicationKeysEnum.STREET.getKey(), structuredAddress.getStreet(), informationContentList);
        informationContentFactory.setInformationContent(CommunicationKeysEnum.CITY.getKey(), structuredAddress.getPostTown(), informationContentList);
        informationContentFactory.setInformationContent(CommunicationKeysEnum.COUNTY.getKey(), structuredAddress.getCounty(), informationContentList);
        int lineNumber = 0;
        for (String addressLine : structuredAddress.getAddressLinePAFData()) {
            if (lineNumber < ADDRESS_LINE_COUNT_3) {
                informationContentFactory.setInformationContent(CommunicationKeysEnum.ADDRESS_LINE.getKey() + ++lineNumber, addressLine, informationContentList);
            }
        }
        if (structuredAddress.getPostCodeIn() != null && structuredAddress.getPostCodeOut() != null) {
            informationContentFactory.setInformationContent(CommunicationKeysEnum.PRODUCT_POSTCODE.getKey(), structuredAddress.getPostCodeOut() + structuredAddress.getPostCodeIn(), informationContentList);
        }
        return informationContentList;
    }

    private String getKeyName(String attributeCode) {
        for (CommunicationKeysEnum keyEnum : CommunicationKeysEnum.values()) {
            if (keyEnum.name().equalsIgnoreCase(attributeCode)) {
                return keyEnum.getKey();
            }
        }
        return null;
    }

}
