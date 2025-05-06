package com.lloydsbanking.salsa.apasa.service.fulfil.convert;

import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Req;
import org.springframework.stereotype.Component;

@Component
public class E502RequestFactory {
    public static final int MAX_REPEAT_GROUP_QTY = 10;

    public E502Req convert(String accountNo, String rollOverAccountNo) {
        E502Req req = new E502Req();
        req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QTY);
        req.setCBSAccountNoId(accountNo);
        req.setRollOverAccountNo(rollOverAccountNo);
        return req;
    }
}
