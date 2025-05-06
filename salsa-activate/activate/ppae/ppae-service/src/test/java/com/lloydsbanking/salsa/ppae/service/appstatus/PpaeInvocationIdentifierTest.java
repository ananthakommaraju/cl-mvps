package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class PpaeInvocationIdentifierTest {

    PpaeInvocationIdentifier ppaeInvocationIdentifier;
    boolean invokeActivateProductArrangementFlag;
    boolean invokeModifyProductArrangementFlag;

    @Before
    public void setUp() {
        invokeActivateProductArrangementFlag = false;
    }

    @Test
    public void testSetterWithFalseValue() {
        invokeModifyProductArrangementFlag = false;
        ppaeInvocationIdentifier = new PpaeInvocationIdentifier();
        ppaeInvocationIdentifier.setInvokeActivateProductArrangementFlag(invokeActivateProductArrangementFlag);
        ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(invokeModifyProductArrangementFlag);
        assertEquals(false, ppaeInvocationIdentifier.getInvokeActivateProductArrangementFlag());
        assertEquals(false, ppaeInvocationIdentifier.getInvokeModifyProductArrangementFlag());
    }

    @Test
    public void testInvokeActivateProductArrangementFlagWithTrueValue() {
        invokeModifyProductArrangementFlag = true;
        ppaeInvocationIdentifier = new PpaeInvocationIdentifier();
        ppaeInvocationIdentifier.setInvokeActivateProductArrangementFlag(invokeActivateProductArrangementFlag);
        ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(invokeModifyProductArrangementFlag);
        assertEquals(false, ppaeInvocationIdentifier.getInvokeActivateProductArrangementFlag());
        assertEquals(true, ppaeInvocationIdentifier.getInvokeModifyProductArrangementFlag());
    }

}
