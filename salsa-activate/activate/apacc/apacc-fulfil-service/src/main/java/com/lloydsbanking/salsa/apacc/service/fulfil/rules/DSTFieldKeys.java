package com.lloydsbanking.salsa.apacc.service.fulfil.rules;


public enum DSTFieldKeys {

    APNO("APNO"),
    PROD("PROD"),
    MAC("MAC"),
    CSLM("CSLM"),
    PTLE("PTLE"),
    PFNM("PFNM"),
    PMNM("PMNM"),
    PSRN("PSRN"),
    PDOB("PDOB"),
    SEXP("SEXP"),
    PAD1("PAD1"),
    PAD2("PAD2"),
    PAD3("PAD3"),
    PAD4("PAD4"),
    PCDE("PCDE"),
    TACA("TACA"),
    PPA1("PPA1"),
    PPA2("PPA2"),
    PPA3("PPA3"),
    PPTN("PPTN"),
    PPCD("PPCD"),
    PPCY("PPCY"),
    TAPA("TAPA"),
    HSTD("HSTD"),
    TELE("TELE"),
    WSTD("WSTD"),
    WTEL("WTEL"),
    MPHN("MPHN"),
    EMAL("EMAL"),
    PNAT("PNAT"),
    MSTS("MSTS"),
    RSTS("RSTS"),
    APSC("APSC"),
    APAC("APAC"),
    BKYM("BKYM"),
    GINC("GINC"),
    ESTS("ESTS"),
    OCRD("OCRD"),
    ENAM("ENAM"),
    TEMP("TEMP"),
    BTRF("BTRF"),
    BTA("BTA"),
    BTA1("BTA1"),
    BTA2("BTA2"),
    BTA3("BTA3"),
    BTA4("BTA4"),
    DDSC("DDSC"),
    DDAC("DDAC"),
    DDSP("DDSP"),
    DDMP("DDMP"),
    CUS2("CUS2"),
    ATLE("ATLE"),
    AFNM("AFNM"),
    AMNM("AMNM"),
    ASRN("ASRN"),
    SDOB("SDOB"),
    SEXA("SEXA"),
    SNAT("SNAT"),
    SPRO("SPRO"),
    NCNO("NCNO"),
    APDT("APDT"),
    PCCI("PCCI"),
    TRAD("TRAD"),
    AFFG("AFFG"),
    PSIG("PSIG"),
    PRES("PRES"),
    PCRY("PCRY"),
    PTWN("PTWN"),
    PCTD("PCTD"),
    INCD("INCD"),
    PPCT("PPCT"),
    PCTY("PCTY");

    private String fieldKey;

    DSTFieldKeys(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getKey() {
        return fieldKey;
    }

}
