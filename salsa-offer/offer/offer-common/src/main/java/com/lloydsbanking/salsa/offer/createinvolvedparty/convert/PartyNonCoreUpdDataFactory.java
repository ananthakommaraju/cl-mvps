package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.downstream.ocis.client.f062.RequestBuilderUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PartyNonCoreUpdDataType;
import lib_sim_bo.businessobjects.Individual;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class PartyNonCoreUpdDataFactory {

    private static final String DEFAULT_STAFF_INDICATOR = "0";

    private static final String SEPARATED = "003";

    private static final String WIDOWED = "005";

    private RequestBuilderUtility utility = new RequestBuilderUtility();

    public PartyNonCoreUpdDataType generatePartyNonCoreUpdData(Individual isPlayedBy) {
        PartyNonCoreUpdDataType partyNonCoreUpdDataType = new PartyNonCoreUpdDataType();
        partyNonCoreUpdDataType.setStaffIn(DEFAULT_STAFF_INDICATOR);
        partyNonCoreUpdDataType.setMaritalStatusCd(getMaritalStatusCode(isPlayedBy.getMaritalStatus()));
        partyNonCoreUpdDataType.setEmploymentStatusCd(utility.getEmploymentStatus(isPlayedBy.getEmploymentStatus()));
        if (!StringUtils.isEmpty(isPlayedBy.getOccupation())) {
            if (NumberUtils.isNumber(isPlayedBy.getOccupation())) {
                partyNonCoreUpdDataType.setOccupationalRoleCd(Short.parseShort(isPlayedBy.getOccupation()));
            }
        }
        else {
            partyNonCoreUpdDataType.setOccupationalRoleCd((short) 0);
        }

        if (!StringUtils.isEmpty(isPlayedBy.getResidentialStatus())) {
            if (NumberUtils.isNumber(isPlayedBy.getResidentialStatus())) {
                partyNonCoreUpdDataType.setResidStatusCd(Short.valueOf(isPlayedBy.getResidentialStatus()));
            }
        }

        return partyNonCoreUpdDataType;
    }

    private Short getMaritalStatusCode(String maritalStatus) {
        if (!StringUtils.isEmpty(maritalStatus)) {
            if (maritalStatus.equals(SEPARATED)) {
                return RequestBuilderUtility.MARITAL_STATUS_CD_SEPARATED;
            }
            else if (maritalStatus.equals(WIDOWED)) {
                return RequestBuilderUtility.MARITAL_STATUS_CD_WIDOWED;
            }
            else {
                return Short.parseShort(maritalStatus);
            }
        }
        return null;
    }

}
