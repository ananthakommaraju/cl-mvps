package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import lib_sim_bo.businessobjects.ExtSysProdFamilyIdentifier;
import lib_sim_bo.businessobjects.ProductFamily;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OfferToRpcRequestConverter {
    @Autowired
    AsmResponseToProductFamilyConverter asmResponseToProductFamilyConverter;

    public RetrieveProductConditionsRequest convertOfferToRpcRequest(F205Resp f205Resp, RequestHeader requestHeader) {
        List<ProductFamily> productFamily = asmResponseToProductFamilyConverter.creditScoreResponseToProductFamilyConverter(f205Resp);
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        rpcRequest.setHeader(requestHeader);

        if ((productFamily != null && !productFamily.isEmpty() && productFamily.get(0) != null && !checkExtSysProdFamilyIdentifier(productFamily))) {
            rpcRequest.getProductFamily().addAll(productFamily);
        }

        return rpcRequest;
    }

    private boolean checkExtSysProdFamilyIdentifier(List<ProductFamily> productFamily) {
        List<ExtSysProdFamilyIdentifier> extSysProdFamilyIdentifierList = productFamily.get(0).getExtsysprodfamilyidentifier();
        boolean isExtSysProdFamIdentifier = extSysProdFamilyIdentifierList == null ||
                extSysProdFamilyIdentifierList.isEmpty() || extSysProdFamilyIdentifierList.get(0) == null;
        return isExtSysProdFamIdentifier;

    }
}
