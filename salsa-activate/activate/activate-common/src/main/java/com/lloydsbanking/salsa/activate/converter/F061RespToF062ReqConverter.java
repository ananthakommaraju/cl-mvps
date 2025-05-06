package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062RequestBuilder;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.PartyNonCoreUpdDataBuilder;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.PersonalUpdDataBuilder;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.KYCNonCorePartyData;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.PersonalData;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PartyNonCoreUpdDataType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PersonalUpdDataType;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Customer;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class F061RespToF062ReqConverter {
    private static final Logger LOGGER = Logger.getLogger(F061RespToF062ReqConverter.class);

    public F062Req convert(F061Resp f061Resp, AssessmentEvidence assessmentEvidence, Customer customer) {
        LOGGER.info("Entering F061 Response to F062 Request Converter");
        F062RequestBuilder f062RequestBuilder = new F062RequestBuilder();

        long partyId = f061Resp.getPartyEnqData().getPersonalData() != null ? f061Resp.getPartyEnqData().getPersonalData().getPartyId() : 0;
        f062RequestBuilder.defaults()
                .personalUpdBuilderAndPartyTI(getPersonalUpdDataType(f061Resp, customer), getPartyNonCoreUpdData(customer, f061Resp), partyId)
                .partyUpdData(Arrays.asList(assessmentEvidence), f061Resp.getPartyEnqData().getEvidenceData(), null, null);

        KYCNonCorePartyData kycNonCorePartyData = f061Resp.getPartyEnqData().getKYCNonCorePartyData();
        if (kycNonCorePartyData != null) {
            f062RequestBuilder.kYCNonCorePartyUpdData(kycNonCorePartyData.getCurrEmployerStartDt(), kycNonCorePartyData.getEmployerNm(), kycNonCorePartyData.getNonCorePartyAuditData().getAuditDt(), kycNonCorePartyData.getNonCorePartyAuditData().getAuditTm());
        }

        String countryCode = null, auditDate = null, auditTime = null;
        String firstNationalityCd = null, firstNtnAuditDate = null, firstNtnAuditTime = null;
        if (f061Resp.getPartyEnqData().getKYCPartyData() != null) {
            if (f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes() != null) {
                countryCode = f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().getCountryOfResidCd();
                if (f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().getCtyResAuditData() != null) {
                    auditDate = f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().getCtyResAuditData().getAuditDt();
                    auditTime = f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().getCtyResAuditData().getAuditTm();
                }
            }

            if (f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn() != null) {
                firstNationalityCd = f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().getFirstNationltyCd();
                if (f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().getFrstNtnAuditData() != null) {
                    firstNtnAuditDate = f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().getFrstNtnAuditData().getAuditDt();
                    firstNtnAuditTime = f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().getFrstNtnAuditData().getAuditTm();
                }
            }
        }
        f062RequestBuilder.kYCPartyUpdData(auditDate, auditTime, firstNtnAuditDate, firstNtnAuditTime, customer.getIsPlayedBy().getNationality(), firstNationalityCd, countryCode);
        LOGGER.info("Exiting F061 Response to F062 Request Converter");
        return f062RequestBuilder.build();
    }

    private PartyNonCoreUpdDataType getPartyNonCoreUpdData(Customer customer, F061Resp f061Resp) {
        PartyNonCoreUpdDataBuilder partyNonCoreUpdDataBuilder = new PartyNonCoreUpdDataBuilder();
        String auditDate = null, auditTime = null;
        if (f061Resp.getPartyEnqData().getPartyNonCoreData() != null) {
            partyNonCoreUpdDataBuilder.setStaffDetails(f061Resp.getPartyEnqData().getPartyNonCoreData().getStaffIn(), f061Resp.getPartyEnqData().getPartyNonCoreData().getStaffMemberNo());
            if (f061Resp.getPartyEnqData().getPartyNonCoreData().getNonCoreAuditData() != null) {
                auditDate = f061Resp.getPartyEnqData().getPartyNonCoreData().getNonCoreAuditData().getAuditDt();
                auditTime = f061Resp.getPartyEnqData().getPartyNonCoreData().getNonCoreAuditData().getAuditTm();
            }
        }

        partyNonCoreUpdDataBuilder.residentialStatusCdAuditDtAuditTm(customer.getIsPlayedBy().getResidentialStatus(), auditDate, auditTime)
                .occupationalRoleCd(customer.getIsPlayedBy().getOccupation())
                .employmentStatusCd(customer.getIsPlayedBy().getEmploymentStatus())
                .maritalStatusCd(customer.getIsPlayedBy().getMaritalStatus());

        return partyNonCoreUpdDataBuilder.build();
    }

    private PersonalUpdDataType getPersonalUpdDataType(F061Resp f061Resp, Customer customer) {
        PersonalUpdDataBuilder personalUpdDataBuilder = new PersonalUpdDataBuilder();
        personalUpdDataBuilder.birthDtGenderCdPartyTypeCd(new DateFactory().convertXMLGregorianToDateFormat(customer.getIsPlayedBy().getBirthDate()), customer.getIsPlayedBy().getGender());
        PersonalData personalData = f061Resp.getPartyEnqData().getPersonalData();
        if (personalData != null) {
            List<String> middleNames = new ArrayList<>();
            if (!CollectionUtils.isEmpty(customer.getIsPlayedBy().getIndividualName()) && customer.getIsPlayedBy().getIndividualName().get(0) != null) {
                middleNames.addAll(customer.getIsPlayedBy().getIndividualName().get(0).getMiddleNames());
            }
            String thirdForeNm = getThirdForeNm(personalData.getThirdForeNm(), middleNames);
            personalUpdDataBuilder.dobGenderNameKYCNonCoreAuditUpdDataPartyId(personalData.getDOBAuditData().getAuditDt(), personalData.getDOBAuditData().getAuditTm(),
                    personalData.getGenderAuditData().getAuditDt(), personalData.getGenderAuditData().getAuditTm(),
                    personalData.getNameAuditData().getAuditDt(), personalData.getNameAuditData().getAuditTm());
            personalUpdDataBuilder.namesAndPartyTI(personalData.getPartyTl(), personalData.getSurname(), personalData.getFirstForeNm(), Arrays.asList(personalData.getSecondForeNm(), thirdForeNm));
        }
        return personalUpdDataBuilder.build();
    }

    private String getThirdForeNm(String prsnlDataThirdForeNm, List<String> middleNames) {
        String thirdFrName;
        if (!CollectionUtils.isEmpty(middleNames) && middleNames.size() > 1) {
            thirdFrName = middleNames.get(1);
        } else if (!StringUtils.isEmpty(prsnlDataThirdForeNm)) {
            thirdFrName = prsnlDataThirdForeNm;
        } else {
            thirdFrName = "";
        }
        return thirdFrName;
    }

}