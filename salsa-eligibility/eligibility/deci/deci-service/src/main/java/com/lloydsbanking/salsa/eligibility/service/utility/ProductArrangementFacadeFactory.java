package com.lloydsbanking.salsa.eligibility.service.utility;

import org.apache.commons.collections.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public final class ProductArrangementFacadeFactory {
    private ProductArrangementFacadeFactory() {
    }

    public static <T> List<ProductArrangementFacade> createProductArrangementFacade(List<T> productArrangementList) {
        List<ProductArrangementFacade> productArrangementFacadeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productArrangementList)) {
            for (T productArrangement : productArrangementList) {
                ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
                productArrangementFacadeList.add(productArrangementFacade);
            }
        }
        return productArrangementFacadeList;
    }

}
