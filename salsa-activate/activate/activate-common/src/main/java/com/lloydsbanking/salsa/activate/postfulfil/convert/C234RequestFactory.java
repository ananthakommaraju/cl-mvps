package com.lloydsbanking.salsa.activate.postfulfil.convert;


import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Req;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.DetailedPartyInfo;
import lib_sim_bo.businessobjects.Customer;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class C234RequestFactory {


    private static final short OCIS_EXT_SYS_ID = 19;

    public C234Req convert(Customer customer) {

        C234Req c234Req = new C234Req();
        c234Req.setExtSysId(OCIS_EXT_SYS_ID);
        c234Req.setMaxRepeatGroupQy((short) 0);

        DetailedPartyInfo detailedPartyInfo = new DetailedPartyInfo();

        detailedPartyInfo.setPartyId(Long.parseLong(customer.getCustomerIdentifier()));
        String nationalInsNo = formatNationalInsNum(customer.getIsPlayedBy().getNationalInsuranceNumber());
        detailedPartyInfo.setNationalInsNo(nationalInsNo);
        detailedPartyInfo.setExtSysId((short) 0);
        detailedPartyInfo.setExtPartyIdTx("");
        detailedPartyInfo.setPartyUpdateAuditDt("");
        detailedPartyInfo.setPartyCreateAuditTm("");
        detailedPartyInfo.setPartyStatusCdChangeIn((short) 0);
        detailedPartyInfo.setPartyStatusCd("");
        detailedPartyInfo.setPartyCreateDtChangeIn((short) 0);
        detailedPartyInfo.setPartyCreateDt("");
        detailedPartyInfo.setPartyCreateAuditTm("");
        detailedPartyInfo.setPartyCreateAuditDt(null);
        detailedPartyInfo.setBirthDtChangeIn((short) 0);
        detailedPartyInfo.setBirthDt("");
        detailedPartyInfo.setBirthAuditDt("");
        detailedPartyInfo.setBirthAuditTm("");
        detailedPartyInfo.setDeathNotifiedDtChangeIn((short) 0);
        detailedPartyInfo.setDeathNotifiedDt("");
        detailedPartyInfo.setDeceasedDtChangeIn((short) 0);
        detailedPartyInfo.setDeceasedDt("");
        detailedPartyInfo.setDeathAuditDt("");
        detailedPartyInfo.setDeathAuditTm("");
        detailedPartyInfo.setGenderCdChangeIn((short) 0);
        detailedPartyInfo.setGenderCd("");
        detailedPartyInfo.setGenderAuditDt("");
        detailedPartyInfo.setGenderAuditTm("");
        detailedPartyInfo.setPartyTlChangeIn((short) 0);
        detailedPartyInfo.setPartyTl("");
        detailedPartyInfo.setSurnameChangeIn((short) 0);
        detailedPartyInfo.setSurname("");
        detailedPartyInfo.setFirstItChangeIn((short) 0);
        detailedPartyInfo.setFirstIt("");
        detailedPartyInfo.setSecondItChangeIn((short) 0);
        detailedPartyInfo.setSecondIt("");
        detailedPartyInfo.setThirdItChangeIn((short) 0);
        detailedPartyInfo.setThirdIt("");
        detailedPartyInfo.setFourthItChangeIn((short) 0);
        detailedPartyInfo.setFourthIt("");
        detailedPartyInfo.setFifthItChangeIn((short) 0);
        detailedPartyInfo.setFifthIt("");
        detailedPartyInfo.setFirstForeNmChangeIn((short) 0);
        detailedPartyInfo.setFirstForeNm("");
        detailedPartyInfo.setSecondForeNmChangeIn((short) 0);
        detailedPartyInfo.setSecondForeNm("");
        detailedPartyInfo.setThirdForeNmChangeIn((short) 0);
        detailedPartyInfo.setThirdForeNm("");
        detailedPartyInfo.setGenerationTxChangeIn((short) 0);
        detailedPartyInfo.setGenerationTx("");
        detailedPartyInfo.setNameSuffixTxChangeIn((short) 0);
        detailedPartyInfo.setNameSuffixTx("");
        detailedPartyInfo.setNameAuditDt("");
        detailedPartyInfo.setNameAuditTm("");

        detailedPartyInfo.setArmedForcesSerialNoChangeIn((short) 0);
        detailedPartyInfo.setArmedForcesSerialNo("");
        detailedPartyInfo.setArmedForcesAuditDt("");
        detailedPartyInfo.setArmedForcesAuditTm("");
        detailedPartyInfo.setNationalInsNoChangeIn((short) 1);

        detailedPartyInfo.setCorresSalutatnTxChangeIn((short) 0);
        detailedPartyInfo.setCorresSalutatnTx("");
        detailedPartyInfo.setSalutationAuditDt("");
        detailedPartyInfo.setSalutationAuditDt("");
        detailedPartyInfo.setSalutationAuditTm("");
        detailedPartyInfo.setPartyCreateAuditDt("");
        detailedPartyInfo.setPartyUpdateAuditTm("");
        c234Req.setDetailedPartyInfo(detailedPartyInfo);
        return c234Req;
    }

    private String formatNationalInsNum(String nationalInsNum) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(nationalInsNum);
        return matcher.find() ? matcher.replaceAll("") : nationalInsNum;
    }
}
