package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.brand.Brand;
import lib_sbo_cardacquire.businessojects.AWD;
import lib_sbo_cardacquire.businessojects.FieldGroup;
import lib_sbo_cardacquire.businessojects.Transaction;
import lib_sbo_cardacquire.interfaces.cardacquiremqservice.DST;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DSTRequestFactory {

    private static final String CCA_STATUS = "GALCOMP";
    private static final String CCA_WORKTYPE = "NBINTBANK";
    private static final String CCA_JOBNAME = "GxyAWDCreate";
    private static final String TRADE_STATUS = "UNINDEXED";
    private static final String CCA_LLOY_BUSINESSAREA = "CSNEWBUS";
    private static final String CCA_BOS_BUSINESSAREA = "BOSNEWBUS";
    private static final String CCA_HLX_BUSINESSAREA = "HFXNEWBUS";
    private static final String CCA_VER_BUSINESSAREA = "VERDE";
    private static final String STATUS_YES = "Y";
    private static final String HOST_AWD = "AWD";
    private static final String USERID = "DSTSETUP";
    private static final String SOURCE_TYPE_CCA = "CCA";

    @Autowired
    DSTFieldFactory dstFieldFactory;

    public DST convert(FinanceServiceArrangement financeServiceArrangement, String channelId, byte[] image) {
        DST dst = new DST();
        dst.setJobName(CCA_JOBNAME);
        dst.setReadable(STATUS_YES);
        AWD awd = new AWD();
        awd.setHost(HOST_AWD);
        awd.setUserid(USERID);
        awd.setTransaction(getTransaction(financeServiceArrangement, channelId, image));
        dst.setAWD(awd);
        return dst;
    }

    private Transaction getTransaction(FinanceServiceArrangement financeServiceArrangement, String channelId, byte[] image) {
        Transaction transaction = new Transaction();
        transaction.setCreate(STATUS_YES);
        if (ActivateCommonConstant.ApplicationType.NEW.equals(financeServiceArrangement.getApplicationType())) {
            transaction.setSourceType(SOURCE_TYPE_CCA);
        }
        transaction.setWorkType(CCA_WORKTYPE);
        transaction.setStatus(getTransactionStatus(financeServiceArrangement.getApplicationType()));
        transaction.setBusinessArea(getBusinessArea(channelId));
        transaction.setImage(image);
        FieldGroup fieldGroup = new FieldGroup();
        fieldGroup.getField().addAll(dstFieldFactory.getFieldList(financeServiceArrangement));
        transaction.setFieldGroup(fieldGroup);
        return transaction;
    }

    private String getTransactionStatus(String appType) {
        return ActivateCommonConstant.ApplicationType.TRADE.equals(appType) ? TRADE_STATUS : CCA_STATUS;
    }

    private String getBusinessArea(String channelId) {
        String businessArea = null;
        if (Brand.LLOYDS.asString().equalsIgnoreCase(channelId)) {
            businessArea = CCA_LLOY_BUSINESSAREA;
        } else if (Brand.HALIFAX.asString().equalsIgnoreCase(channelId)) {
            businessArea = CCA_HLX_BUSINESSAREA;
        } else if (Brand.BANK_OF_SCOTLAND.asString().equalsIgnoreCase(channelId)) {
            businessArea = CCA_BOS_BUSINESSAREA;
        } else if (Brand.TRUSTEE_SAVINGS_BANK.asString().equalsIgnoreCase(channelId)) {
            businessArea = CCA_VER_BUSINESSAREA;
        }
        return businessArea;
    }

}
