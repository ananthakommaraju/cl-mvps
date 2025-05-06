package com.lloydsbanking.salsa.opacc.service.downstream;

import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataRequest;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataResponse;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptionType;
import com.lloydsbanking.salsa.soap.encrpyt.objects.Indetails;
import lib_sim_bo.businessobjects.AccessToken;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EncryptDataService {

    private static final String ENCRYPT_INP_CODE = "base64";
    private static final String ENCRYPT_TYPE = "ASYMM";

    private static final String GROUP_CODE_ENCRYPT_KEY_GROUP = "ENCRYPT_KEY_GROUP";

    @Autowired
    EncryptDataRetriever encryptDataRetriever;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    LookupDataRetriever offerLookupDataRetriever;

    @Autowired
    ExceptionUtility exceptionUtility;

    private static final Logger LOGGER = Logger.getLogger(EncryptDataService.class);

    public void retrieveEncryptData(AccessToken accessToken, RequestHeader requestHeader) throws OfferException {
        LOGGER.info("Entering EncryptDataService");
        if (accessToken != null) {
            String encryptKey = getEncryptKeyForEncryptDataRequest(requestHeader.getChannelId());
            LOGGER.info("Encrypt Key for EncryptDataRequest: " + encryptKey);
            EncryptDataRequest encryptDataRequest = createEncryptDataRequest(accessToken.getMemorableInfo(), encryptKey);
            EncryptDataResponse encryptDataResponse = null;
            try {
                encryptDataResponse = encryptDataRetriever.retrieveEncryptDataResponse(encryptDataRequest, requestHeader);
            } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
                throw new OfferException(resourceNotAvailableErrorMsg);
            }
            try {
                accessToken.setMemorableInfo(toSerializedString(encryptDataResponse));
            } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
                throw new OfferException(internalServiceErrorMsg);
            }
        }
        LOGGER.info("Exiting EncryptDataService");
    }

    private EncryptDataRequest createEncryptDataRequest(String itemCardNum, String encryptKey) {
        EncryptDataRequest encryptDataRequest = new EncryptDataRequest();
        Indetails indetails = new Indetails();
        indetails.setIntext(itemCardNum);
        indetails.setEncryptKey(encryptKey);
        indetails.setEncryptType(EncryptionType.valueOf(ENCRYPT_TYPE));
        indetails.setInpEncode(ENCRYPT_INP_CODE);
        encryptDataRequest.getIndetails().add(indetails);
        return encryptDataRequest;
    }

    private String getEncryptKeyForEncryptDataRequest(String channelId) throws OfferException {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(GROUP_CODE_ENCRYPT_KEY_GROUP);
        List<ReferenceDataLookUp> lookupList = null;
        try {
            lookupList = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, groupCodeList);
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw new OfferException(dataNotAvailableErrorMsg);
        }
        return lookupList.get(0).getLookupValueDesc();
    }

    private String toSerializedString(EncryptDataResponse encryptDataResponse) throws InternalServiceErrorMsg {
        if (null != encryptDataResponse) {
            ByteArrayOutputStream encryptDataResponseToSerializedString = new ByteArrayOutputStream();
            try {
                JAXBContext jc = JAXBContext.newInstance(String.class, EncryptDataResponse.class);
                JAXBIntrospector introspector = jc.createJAXBIntrospector();
                Marshaller marshaller = jc.createMarshaller();
                if (null == introspector.getElementName(encryptDataResponse)) {
                    JAXBElement jaxbElement = new JAXBElement(new QName("EncryptDataResponse"), Object.class, encryptDataResponse);
                    marshaller.marshal(jaxbElement, encryptDataResponseToSerializedString);
                } else {
                    marshaller.marshal(encryptDataResponse, encryptDataResponseToSerializedString);
                }
            } catch (JAXBException exception) {
                LOGGER.error("Exception occurred in Encrypt Data Service; ", exception);
                throw exceptionUtility.internalServiceError(null, exception.getMessage());
            }
            String encryptedString = null;
            try {
                encryptedString = new String(encryptDataResponseToSerializedString.toByteArray(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.info("EncryptDataService: toSerializedString(), Exception occurred during conversion of encrypted data to String; " + e);
            }
            return encryptedString;
        }
        return null;
    }
}
