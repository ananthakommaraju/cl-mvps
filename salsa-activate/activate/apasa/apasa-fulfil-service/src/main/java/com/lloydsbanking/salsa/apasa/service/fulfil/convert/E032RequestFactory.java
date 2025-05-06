package com.lloydsbanking.salsa.apasa.service.fulfil.convert;

import com.lloydsbanking.salsa.downstream.cbs.client.e032.E032RequestBuilder;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Req;
import org.springframework.stereotype.Component;

@Component
public class E032RequestFactory {
    public E032Req convert(String sortCode, String accNo,String beneficiaryAccountNumber,String beneficiarySortCode, String transactionName) {
        E032RequestBuilder builder = new E032RequestBuilder();
        builder.defaults();
        builder.beneficiaryNameTx(transactionName);
        builder.cbsAccountNoId(sortCode, accNo);
        builder.fullBeneficiaryAccountNumber(beneficiarySortCode,beneficiaryAccountNumber);
        return builder.build();
    }
}
