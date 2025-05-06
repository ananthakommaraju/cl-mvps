package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;

public interface AskQuestion {
    boolean ask() throws EligibilityException;
}
