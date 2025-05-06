package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lib_sim_bo.businessobjects.Product;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

import static java.lang.Math.abs;

public class QuestionHasCreditCardStartDatePriorToThreshold extends AbstractProductListQuestion implements AskQuestion  {

    private DateFactory dateFactory = new DateFactory();

    private static final String PRODUCT_TYPE_CC = "3";

    public static QuestionHasCreditCardStartDatePriorToThreshold pose() {
        return new QuestionHasCreditCardStartDatePriorToThreshold();
    }

    public boolean ask() {
        long thresholdVal = Long.valueOf(threshold);

        Date now = new Date();

        for (ProductArrangementFacade productArrangement : productArrangements) {

            final Product associatedProduct = productArrangement.getAssociatedProduct();

            final XMLGregorianCalendar arrangementStartDate = productArrangement.getArrangementStartDate();

            if (null != associatedProduct && null != associatedProduct.getProductType() &&
                    PRODUCT_TYPE_CC.equals(associatedProduct.getProductType()) && null != arrangementStartDate) {

                Date startDate = arrangementStartDate.toGregorianCalendar().getTime();

                if ((abs(dateFactory.differenceInDays(now, startDate)) <= thresholdVal)) {
                    return true;
                }
            }
        }
        return false;
    }
}
