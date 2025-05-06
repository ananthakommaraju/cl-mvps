package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.AlternateID;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.RequestHeader;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.Individual;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.InvolvedPartyObjectReference;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RetrieveInvolvedPartyDetailsRequestFactory {
    public static final String EXT_SYS_SALSA = "19";

    public RetrieveInvolvedPartyDetailsRequest convert(String customerIdentifier) {
        RetrieveInvolvedPartyDetailsRequest request = new RetrieveInvolvedPartyDetailsRequest();
        Individual individual = new Individual();
        InvolvedPartyObjectReference objectReference = new InvolvedPartyObjectReference();
        if (!StringUtils.isEmpty(customerIdentifier)) {
            objectReference.setIdentifier(customerIdentifier);
        }
        AlternateID alternateID = new AlternateID();
        alternateID.setExternalSystemIdentifier(EXT_SYS_SALSA);
        objectReference.getAlternateId().add(alternateID);
        individual.setObjectReference(objectReference);
        request.setInvolvedParty(individual);
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setDatasourceName(EXT_SYS_SALSA);
        request.setRequestHeader(requestHeader);
        return request;

    }


}
