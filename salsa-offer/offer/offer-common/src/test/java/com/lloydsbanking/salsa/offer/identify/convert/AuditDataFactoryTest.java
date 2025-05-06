package com.lloydsbanking.salsa.offer.identify.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import lib_sim_bo.businessobjects.AuditData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class AuditDataFactoryTest {

    private AuditDataFactory auditDataFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        auditDataFactory = new AuditDataFactory();
        testDataHelper = new TestDataHelper();

    }

    @Test
    public void testGetAuditData() {

        PartyEnqData partyEnqData = new PartyEnqData();
        partyEnqData.setAddressData(new AddressData());
        partyEnqData.getAddressData().setAddressAuditData(new AddressAuditData());
        partyEnqData.getAddressData().getAddressAuditData().setAuditTm("auditTm1");
        partyEnqData.getAddressData().getAddressAuditData().setAuditDt("auditDt1");

        partyEnqData.setEvidenceData(new EvidenceData());
        partyEnqData.getEvidenceData().getAddrEvid().add(new AddrEvid());
        partyEnqData.getEvidenceData().getAddrEvid().get(0).setAddrEvidAuditData(new AddrEvidAuditData());
        partyEnqData.getEvidenceData().getAddrEvid().get(0).getAddrEvidAuditData().setAuditDt("auditDt2");
        partyEnqData.getEvidenceData().getAddrEvid().get(0).getAddrEvidAuditData().setAuditTm("auditTm2");

        partyEnqData.getEvidenceData().getPartyEvid().add(new PartyEvid());
        partyEnqData.getEvidenceData().getPartyEvid().get(0).setPartyEvidAuditData(new PartyEvidAuditData());
        partyEnqData.getEvidenceData().getPartyEvid().get(0).getPartyEvidAuditData().setAuditDt("auditDt3");
        partyEnqData.getEvidenceData().getPartyEvid().get(0).getPartyEvidAuditData().setAuditTm("auditTm3");

        Collection auditDataList = auditDataFactory.getAuditData(partyEnqData);
        List<AuditData> auditDataList1 = (List<AuditData>) auditDataList;
        assertEquals("auditTm1", auditDataList1.get(0).getAuditTime());
        assertEquals("auditDt1", auditDataList1.get(0).getAuditDate());
        assertEquals("ADDRESS", auditDataList1.get(0).getAuditType());

        assertEquals("auditTm3", auditDataList1.get(1).getAuditTime());
        assertEquals("auditDt3", auditDataList1.get(1).getAuditDate());
        assertEquals("PARTY_EVIDENCE", auditDataList1.get(1).getAuditType());

        assertEquals("auditTm2", auditDataList1.get(2).getAuditTime());
        assertEquals("auditDt2", auditDataList1.get(2).getAuditDate());
        assertEquals("ADDRESS_EVIDENCE", auditDataList1.get(2).getAuditType());


    }


}
