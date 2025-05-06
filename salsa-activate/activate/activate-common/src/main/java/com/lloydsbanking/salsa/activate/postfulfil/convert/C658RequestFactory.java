package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Req;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class C658RequestFactory {
    public static final int MAX_REPEAT_GROUP_QY = 1;

    public static final short EXTERNAL_SYS_SALSA = 19;

    public static final short KEY_EXTERNAL_SYS_ID = 2;

    public static final String UK_ADDRESS_TYPE_CD = "001";

    public static final short STATUS_CD_INDICATOR_TRUE = 1;

    C658Req c658Req;

    public C658RequestFactory() {
        c658Req = new C658Req();
    }

    public C658Req convert(ProductArrangement depositArrangement) {
        c658Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        c658Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c658Req.setKeyExtSysId(KEY_EXTERNAL_SYS_ID);
        c658Req.setPtyTlcAddressTypeCd(UK_ADDRESS_TYPE_CD);
        c658Req.setTlcAddressTypeCd(UK_ADDRESS_TYPE_CD);
        c658Req.setAddressStatusCd(UK_ADDRESS_TYPE_CD);
        c658Req.setAddressStatusCdIn(STATUS_CD_INDICATOR_TRUE);
        c658Req.setTlcmmnAddressTxIn(STATUS_CD_INDICATOR_TRUE);
        c658Req.setAuditDt("");
        c658Req.setAuditTm("");
        Customer guardianCustomer = depositArrangement.getGuardianDetails();
        Customer primaryCustomer = depositArrangement.getPrimaryInvolvedParty();
        tlcmmnAddressTx(guardianCustomer, primaryCustomer);
        partyId(guardianCustomer, primaryCustomer);
        return c658Req;
    }

    private void tlcmmnAddressTx(Customer guardianCustomer, Customer primaryCustomer) {

        if (guardianCustomer != null && !StringUtils.isEmpty(guardianCustomer.getEmailAddress())) {
            c658Req.setTlcmmnAddressTx(guardianCustomer.getEmailAddress());
        } else {
            c658Req.setTlcmmnAddressTx(primaryCustomer.getEmailAddress());
        }
    }

    private void partyId(Customer guardianCustomer, Customer primaryCustomer) {

        if (guardianCustomer != null && !StringUtils.isEmpty(guardianCustomer.getCustomerIdentifier()) && !StringUtils.isEmpty(guardianCustomer.getCidPersID())) {
            c658Req.setExtPartyIdTx(guardianCustomer.getCidPersID());
            c658Req.setPartyId(Long.valueOf(guardianCustomer.getCustomerIdentifier()));
        } else {
            c658Req.setExtPartyIdTx(primaryCustomer.getCidPersID());
            if (!StringUtils.isEmpty(primaryCustomer.getCustomerIdentifier())) {
                c658Req.setPartyId(Long.valueOf(primaryCustomer.getCustomerIdentifier()));
            }
        }
    }

}
