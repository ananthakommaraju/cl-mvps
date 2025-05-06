package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.downstream.ocis.client.f447.F447RequestBuilder;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import org.apache.log4j.Logger;

import java.util.List;

public class EnquirePartyIdRequestFactory {

    public static final int MAX_NUM_MIDDLE_NAMES = 3;
    private static final String INTERNAL_SERVICE_ERROR_CODE = "82001";
    private static final Logger LOGGER = Logger.getLogger(EnquirePartyIdRequestFactory.class);

    public F447Req convert(List<PostalAddress> postalAddresses, Individual isPlayedBy, ExceptionUtility exceptionUtility) throws InternalServiceErrorMsg {

        F447RequestBuilder requestBuilder = new F447RequestBuilder();

        if (null != isPlayedBy) {
            convertNames(isPlayedBy, requestBuilder);

            if (null != isPlayedBy.getBirthDate()) {
                requestBuilder.birthDate(isPlayedBy.getBirthDate().toGregorianCalendar().getTime());
            }

            requestBuilder.genderCd(isPlayedBy.getGender());
        } else {
            InternalServiceErrorMsg error = exceptionUtility.internalServiceError(INTERNAL_SERVICE_ERROR_CODE, "The Individual is not set");
            LOGGER.error("Exception occurred while creating request for OCIS F447. Returning InternalServiceError ;", error);
            throw error;
        }

        requestBuilder.postCdAndDelivPointSuffixCd(postalAddresses).defaults();

        return requestBuilder.build();
    }

    private void convertNames(Individual isPlayedBy, F447RequestBuilder requestBuilder) {
        if (null != isPlayedBy.getIndividualName()) {
            IndividualName individualName = isPlayedBy.getIndividualName().get(0);

            if (null != individualName) {
                requestBuilder.surName(individualName.getLastName());
                requestBuilder.firstIt(individualName.getFirstName());
                requestBuilder.secondThirdFouthIt(individualName.getMiddleNames());

                if (null == individualName.getMiddleNames() || individualName.getMiddleNames().isEmpty()) {
                    requestBuilder.foreNm(individualName.getFirstName(), null, null);
                } else if (individualName.getMiddleNames().size() >= MAX_NUM_MIDDLE_NAMES) {
                    requestBuilder.foreNm(individualName.getFirstName(),
                            individualName.getMiddleNames().get(0),
                            individualName.getMiddleNames().get(2));
                } else if (!individualName.getMiddleNames().isEmpty()){
                    requestBuilder.foreNm(individualName.getFirstName(),
                            individualName.getMiddleNames().get(0), null);
                }
            }
        }
    }
}
