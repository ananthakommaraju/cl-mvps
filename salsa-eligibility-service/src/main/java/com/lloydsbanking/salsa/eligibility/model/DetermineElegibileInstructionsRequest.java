package com.lloydsbanking.salsa.eligibility.model;

@Data
public class DetermineElegibileInstructionsRequest {
    protected List<String> candidateInstructions;
    protected List<ProductArrangement> customerArrangements;
    protected Individual individual;
    protected List<BusinessArrangement> businessArrangements;
    protected ArrangementIdentifier selctdArr;
    protected String selctdBusnsId;
}