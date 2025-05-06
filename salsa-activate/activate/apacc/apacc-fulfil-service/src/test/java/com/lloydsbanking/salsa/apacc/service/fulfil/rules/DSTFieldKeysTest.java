package com.lloydsbanking.salsa.apacc.service.fulfil.rules;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class DSTFieldKeysTest {
    @Test
    public void testGetKey() {
        assertEquals("MSTS", DSTFieldKeys.MSTS.getKey());
    }
}
