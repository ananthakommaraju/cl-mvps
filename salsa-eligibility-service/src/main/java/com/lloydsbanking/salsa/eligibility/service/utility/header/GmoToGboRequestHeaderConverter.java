package com.lloydsbanking.salsa.eligibility.service.utility.header;

import lb_gbo_sales.businessobjects.ContextItem;
import lb_gbo_sales.messages.EATraceContextInfo;
import lb_gbo_sales.messages.RequestHeader;
import lb_gbo_sales.messages.SOAPHeader;

import java.util.ArrayList;
import java.util.List;

public class GmoToGboRequestHeaderConverter {
    RequestHeader requestHeader = new RequestHeader();

    public RequestHeader convert(lib_sim_gmo.messages.RequestHeader gmoRequestHeader) {
        requestHeader.setChannelId(gmoRequestHeader.getChannelId());
        requestHeader.setInteractionId(gmoRequestHeader.getInteractionId());
        requestHeader.setBusinessTransaction(gmoRequestHeader.getBusinessTransaction());

        setEaTraceContextInfo(gmoRequestHeader);

        setLloydsHeaders(gmoRequestHeader);

        return requestHeader;
    }

    private void setLloydsHeaders(lib_sim_gmo.messages.RequestHeader gmoRequestHeader) {
        List<SOAPHeader> lloydsHeaders = new ArrayList<>();
        for (lib_sim_gmo.messages.SOAPHeader gmoSoapHeader : gmoRequestHeader.getLloydsHeaders()) {
            SOAPHeader soapHeader = new SOAPHeader();
            soapHeader.setValue(gmoSoapHeader.getValue());
            soapHeader.setName(gmoSoapHeader.getName());
            soapHeader.setNameSpace(gmoSoapHeader.getNameSpace());
            soapHeader.setPrefix(gmoSoapHeader.getPrefix());
            lloydsHeaders.add(soapHeader);
        }

        requestHeader.getLloydsHeaders().addAll(lloydsHeaders);
    }

    private void setEaTraceContextInfo(lib_sim_gmo.messages.RequestHeader gmoRequestHeader) {
        List<ContextItem> contextItemList = new ArrayList();
        if (null != gmoRequestHeader.getEaTraceContextInfo()) {
            for (lib_sim_gmo.messages.ContextItem gmoContextItem : gmoRequestHeader.getEaTraceContextInfo().getContextItems()) {
                ContextItem contextItem = new ContextItem();
                contextItem.setName(gmoContextItem.getName());
                contextItem.setValue(gmoContextItem.getValue());
                contextItemList.add(contextItem);
            }
        }
        EATraceContextInfo eaTraceContextInfo = new EATraceContextInfo();
        eaTraceContextInfo.getContextItems().addAll(contextItemList);
        requestHeader.setEaTraceContextInfo(eaTraceContextInfo);
    }
}
