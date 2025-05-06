package com.lloydsbanking.salsa.ppae.service.appstatus;


import org.apache.log4j.Logger;

public class PpaeInvocationIdentifier {

    private static final Logger LOGGER = Logger.getLogger(PpaeInvocationIdentifier.class);

    boolean invokeActivateProductArrangementFlag;

    boolean invokeModifyProductArrangementFlag;

    public PpaeInvocationIdentifier() {
    }

    public PpaeInvocationIdentifier(boolean invokeModifyProductArrangementFlag, boolean invokeActivateProductArrangementFlag) {
        this.invokeActivateProductArrangementFlag = invokeActivateProductArrangementFlag;
        this.invokeModifyProductArrangementFlag = invokeModifyProductArrangementFlag;
    }

    public void setInvokeActivateProductArrangementFlag(boolean invokeActivateProductArrangementFlag) {
        this.invokeActivateProductArrangementFlag = invokeActivateProductArrangementFlag;
    }

    public boolean getInvokeActivateProductArrangementFlag() {
        return invokeActivateProductArrangementFlag;
    }

    public void setInvokeModifyProductArrangementFlag(boolean invokeModifyProductArrangementFlag) {
        this.invokeModifyProductArrangementFlag = invokeModifyProductArrangementFlag;
    }

    public boolean getInvokeModifyProductArrangementFlag() {
        return invokeModifyProductArrangementFlag;
    }

}




