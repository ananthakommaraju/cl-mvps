package com.lloydsbanking.salsa.ppae.service.rule;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmploymentStatus {
    Map<String, String> employmentStatusMap = new HashMap<>();

    public EmploymentStatus() {
        populateEmploymentStatusMap();
    }

    public String getEmploymentStatusCode(String emplomentStatus) {
        return employmentStatusMap.get(emplomentStatus);
    }

    private void populateEmploymentStatusMap() {
        employmentStatusMap.put("Not Working Disability Benefit", "000");
        employmentStatusMap.put("Self Employed", "001");
        employmentStatusMap.put("Employed Part Time", "002");
        employmentStatusMap.put("Employed", "003");
        employmentStatusMap.put("Not Working No Income", "004");
        employmentStatusMap.put("Full Time Student", "004");
        employmentStatusMap.put("Homemaker", "005");
        employmentStatusMap.put("Not Employed", "000");
        employmentStatusMap.put("Retired", "006");
        employmentStatusMap.put("Other", "007");
        employmentStatusMap.put("Unemployed", "009");
        employmentStatusMap.put("Employed Part Time", "016");
        employmentStatusMap.put("Not Working Unemployed", "018");
        employmentStatusMap.put("Not Working Independent Means", "022");
    }
}
