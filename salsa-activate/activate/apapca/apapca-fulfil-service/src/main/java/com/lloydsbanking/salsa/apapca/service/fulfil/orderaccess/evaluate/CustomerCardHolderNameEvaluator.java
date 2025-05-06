package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.evaluate;

import com.lloydsbanking.salsa.soap.cmas.c808.objects.CardholderNew;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.Initials;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.PlasticType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomerCardHolderNameEvaluator {
    private static final int FORMAT_SURNAME_FIRSTNAME_INITIALS_TITLE = 1;

    private static final int FORMAT_SURNAME_FIRSTINITIAL_INITIALS_TITLE = 2;

    private static final int FORMAT_SURNAME_FIRSTINITIAL_INITIALS = 3;

    private static final int FORMAT_SURNAME_FIRSTINITIAL = 4;

    private static final int FORMAT_SURNAME = 5;

    private static final int FORMAT_SURNAME_TRUNCATED = 6;

    private static final int FORMAT_NAME_TOO_LONG = 99;

    public String getCardHolderName(CardholderNew cardholderNew, PlasticType plasticType) {
        int cardHolderNameFormattingCode = plasticType.getCardholderNmFormattingCd();
        int cardHolderNameMaxLength = plasticType.getMaxCardholderNmLh();
        StringBuffer cardHolderName = new StringBuffer();
        Initials initials = cardholderNew.getInitials();
        String partyTl = cardholderNew.getPartyTl();
        String title = (!StringUtils.isEmpty(partyTl) ? getTitle(partyTl) : "");

        // loop through formatting styles until a format is short enough to fit the name in
        boolean nameIsTooLong;
        do {
            nameIsTooLong = false;
            switch (cardHolderNameFormattingCode) {
                case FORMAT_SURNAME_FIRSTNAME_INITIALS_TITLE:
                    cardHolderName = updateCardHolderNameSurnameFirstnameInitialsTitle(cardholderNew, cardHolderName, initials, title);
                    nameIsTooLong = isNameTooLong(cardHolderName, cardHolderNameMaxLength);
                    cardHolderNameFormattingCode = FORMAT_SURNAME_FIRSTINITIAL_INITIALS_TITLE;
                    break;
                case FORMAT_SURNAME_FIRSTINITIAL_INITIALS_TITLE:
                    cardHolderName = updateCardHolderNameSurnameFirstInitialInitialsTitle(cardholderNew, initials, title);
                    nameIsTooLong = isNameTooLong(cardHolderName, cardHolderNameMaxLength);
                    cardHolderNameFormattingCode = FORMAT_SURNAME_FIRSTINITIAL_INITIALS;
                    break;
                case FORMAT_SURNAME_FIRSTINITIAL_INITIALS:
                    cardHolderName = updateCardHolderNameSurnameFirstInitialInitials(cardholderNew, initials);
                    nameIsTooLong = isNameTooLong(cardHolderName, cardHolderNameMaxLength);
                    cardHolderNameFormattingCode = FORMAT_SURNAME_FIRSTINITIAL;
                    break;
                case FORMAT_SURNAME_FIRSTINITIAL:
                    cardHolderName = updateCardHolderNameSurnameFirstInitial(cardholderNew, initials);
                    nameIsTooLong = isNameTooLong(cardHolderName, cardHolderNameMaxLength);
                    cardHolderNameFormattingCode = FORMAT_SURNAME;
                    break;
                case FORMAT_SURNAME:
                    cardHolderName = new StringBuffer(cardholderNew.getSurname()).append("/");
                    nameIsTooLong = isNameTooLong(cardHolderName, cardHolderNameMaxLength);
                    cardHolderNameFormattingCode = FORMAT_SURNAME_TRUNCATED;
                    break;
                case FORMAT_SURNAME_TRUNCATED:
                    cardHolderName = new StringBuffer(cardholderNew.getSurname().substring(0, cardHolderNameMaxLength - 1)).append("/");
                    nameIsTooLong = isNameTooLong(cardHolderName, cardHolderNameMaxLength);
                    cardHolderNameFormattingCode = FORMAT_NAME_TOO_LONG;
                    break;
                default:
                    break;
            }
        } while (nameIsTooLong);

        return cardHolderName.toString().trim().toUpperCase();

    }

    private StringBuffer updateCardHolderNameSurnameFirstInitial(final CardholderNew cardholderNew, final Initials initials) {
        final StringBuffer cardHolderName;
        cardHolderName = new StringBuffer(cardholderNew.getSurname()).append("/");
        cardHolderName.append(initials != null ? initials.getFirstIt() : "");
        return cardHolderName;
    }

    private StringBuffer updateCardHolderNameSurnameFirstInitialInitials(final CardholderNew cardholderNew, final Initials initials) {
        final StringBuffer cardHolderName;
        cardHolderName = new StringBuffer(cardholderNew.getSurname()).append("/");
        String test = (initials != null ? getInitials(initials).toString().trim() : "");
        cardHolderName.append(test);
        return cardHolderName;
    }

    private StringBuffer updateCardHolderNameSurnameFirstInitialInitialsTitle(final CardholderNew cardholderNew, final Initials initials, final String title) {
        final StringBuffer cardHolderName;
        cardHolderName = new StringBuffer(cardholderNew.getSurname()).append("/");
        StringBuffer finalInitials = (initials != null ? getInitials(initials) : new StringBuffer(""));
        cardHolderName.append(finalInitials.substring(0, finalInitials.length() - 1));
        cardHolderName.append(title);
        return cardHolderName;
    }

    private StringBuffer updateCardHolderNameSurnameFirstnameInitialsTitle(final CardholderNew cardholderNew, StringBuffer cardHolderName, final Initials initials, final String title) {
        StringBuffer holderName = cardHolderName;
        holderName.append(cardholderNew.getSurname()).append("/");
        String firstName = (!StringUtils.isEmpty(cardholderNew.getFirstForeNm()) ? cardholderNew.getFirstForeNm() : "");
        holderName.append(firstName);
        if (initials != null) {
            String finalInitials = getInitials(initials).toString().trim();
            holderName = addInitials(cardholderNew.getFirstForeNm(), finalInitials, holderName);
        }
        holderName.append(title);
        return holderName;
    }

    private StringBuffer getInitials(Initials initials) {
        StringBuffer finalInitials = new StringBuffer("");
        checkInitials(initials.getFirstIt(), finalInitials);
        checkInitials(initials.getSecondIt(), finalInitials);
        checkInitials(initials.getThirdIt(), finalInitials);
        checkInitials(initials.getFourthIt(), finalInitials);
        checkInitials(initials.getFifthIt(), finalInitials);
        return finalInitials;
    }

    private String getTitle(String title) {
        return ("." + title);
    }

    private StringBuffer addInitials(String firstName, String finalInitials, StringBuffer cardHolderName) {
        if (!StringUtils.isEmpty(firstName)) {
            return (finalInitials.length() > 1 ? cardHolderName.append(" ").append(finalInitials.substring(1, finalInitials.length()).trim()) : cardHolderName);
        } else {
            cardHolderName.append(finalInitials.trim());
        }
        return cardHolderName;
    }

    private boolean isNameTooLong(StringBuffer cardHolderName, int cardHolderNameMaxLength) {
        if (cardHolderName.length() > cardHolderNameMaxLength) {

            return true;
        }
        return false;
    }

    private StringBuffer checkInitials(String initials, StringBuffer finalInitials) {
        return (initials != null ? finalInitials.append(initials).append(" ") : finalInitials);
    }

}
