package com.lloydsbanking.salsa.activate.constants;

public enum SMSTemplateEnum {

    STPSAVSUCCESS("STPSAVSUCCESS"),
    STPSAVREMINDER("STPSAVREMINDER"),
    STPSAVFUNDREMIN("STPSAVFUNDREMIN"),
    STPCCRSUCCESS("STPCCRSUCCESS"),
    STPCCRREMINDER("STPCCRREMINDER"),
    STP_SAV_SOURCE("STPSAVINGS"),
    STP_CC_SOURCE("STPCC");

    private final String template;

    SMSTemplateEnum(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return this.template;
    }
}
