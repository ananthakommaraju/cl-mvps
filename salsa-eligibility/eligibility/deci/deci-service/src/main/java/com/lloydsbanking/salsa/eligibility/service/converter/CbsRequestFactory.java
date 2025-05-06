package com.lloydsbanking.salsa.eligibility.service.converter;


import com.lloydsbanking.salsa.soap.cbs.e220.objects.CustNoGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import org.apache.commons.lang3.StringUtils;

public class CbsRequestFactory {
    public static final String DEFAULT_SHADOW_DECISION_SCR_CODE = "A";

    public static final int DEFAULT_SHADOW_DECISION_SCR_FLAG_CODE = 1;

    public static final short MAX_REPEAT_GRP_QY = 10;

    public static final String DEFAULT_SHADOW_DECISION_SCR_CODE_WZ = " ";

    public static final int MAX_LENTH = 12;

    public static final int MAX_LENGTH_WZ = 14;

    public E220Req createE220Request(String sortCode, String customerId) {
        E220Req request = new E220Req();
        request.setCustNoGp(new CustNoGp());
        if (!StringUtils.isEmpty(sortCode)) {
            if (null != customerId && customerId.length() > MAX_LENTH) {
                request.getCustNoGp().setCBSCustNo(customerId.substring(2, customerId.length()));
            }
            else
            {
                request.getCustNoGp().setCBSCustNo(customerId);
            }
            request.getCustNoGp().setNationalSortcodeId(sortCode.substring(0, 2));
            request.setCAPSShdwDecnScrCd(DEFAULT_SHADOW_DECISION_SCR_CODE);
        }
        else if (!StringUtils.isEmpty(customerId)) {
            if (customerId.length() > MAX_LENGTH_WZ) {
                request.getCustNoGp().setCBSCustNo(customerId.substring(2, MAX_LENGTH_WZ));
            }
            else {
                request.getCustNoGp().setCBSCustNo(customerId.substring(2, customerId.length()));
            }
            request.setMaxRepeatGroupQy(MAX_REPEAT_GRP_QY);
            request.getCustNoGp().setNationalSortcodeId(customerId.substring(0, 2));
            request.setCAPSShdwDecnScrCd(DEFAULT_SHADOW_DECISION_SCR_CODE_WZ);
        }

        request.setCAPSShdwDecnScrFlagCd(DEFAULT_SHADOW_DECISION_SCR_FLAG_CODE);
        return request;
    }
}
