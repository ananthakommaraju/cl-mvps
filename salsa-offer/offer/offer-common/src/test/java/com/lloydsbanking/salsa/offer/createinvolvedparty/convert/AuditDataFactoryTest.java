package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddrEvidUpdData1Type;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddressAuditUpdData1Type;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.EvidAuditUpdDataType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PartyEvidUpdData1Type;
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

    @Before
    public void setUp() {
        auditDataFactory = new AuditDataFactory();
    }

    @Test
    public void testGetAuditData() {

        AddressAuditUpdData1Type addressAuditUpdDataType = new AddressAuditUpdData1Type();
        addressAuditUpdDataType.setAuditTm(123456789L);
        addressAuditUpdDataType.setAuditDt(123456789L);

        EvidAuditUpdDataType evidAuditUpdDataType = new EvidAuditUpdDataType();
        evidAuditUpdDataType.getAddrEvidUpdData().add(new AddrEvidUpdData1Type());
        evidAuditUpdDataType.getAddrEvidUpdData().get(0).setAuditDt("auditDt2");
        evidAuditUpdDataType.getAddrEvidUpdData().get(0).setAuditTm("auditTm2");

        evidAuditUpdDataType.getPartyEvidUpdData().add(new PartyEvidUpdData1Type());
        evidAuditUpdDataType.getPartyEvidUpdData().get(0).setAuditDt("auditDt3");
        evidAuditUpdDataType.getPartyEvidUpdData().get(0).setAuditTm("auditTm3");

        Collection auditDataList = auditDataFactory.createAudtitData(addressAuditUpdDataType, evidAuditUpdDataType);
        List<AuditData> auditDataList1 = (List<AuditData>) auditDataList;
        assertEquals("auditTm3", auditDataList1.get(0).getAuditTime());
        assertEquals("auditDt3", auditDataList1.get(0).getAuditDate());
        assertEquals("PARTY_EVIDENCE", auditDataList1.get(0).getAuditType());

        assertEquals("auditTm2", auditDataList1.get(1).getAuditTime());
        assertEquals("auditDt2", auditDataList1.get(1).getAuditDate());
        assertEquals("ADDRESS_EVIDENCE", auditDataList1.get(1).getAuditType());

        assertEquals("123456789", auditDataList1.get(2).getAuditTime());
        assertEquals("123456789", auditDataList1.get(2).getAuditDate());
        assertEquals("ADDRESS", auditDataList1.get(2).getAuditType());


    }

    @Test
    public void testGetAuditDataForDtAndTmLessThanMinimumRequired() {

        AddressAuditUpdData1Type addressAuditUpdDataType = new AddressAuditUpdData1Type();
        addressAuditUpdDataType.setAuditTm(123L);
        addressAuditUpdDataType.setAuditDt(1234L);

        EvidAuditUpdDataType evidAuditUpdDataType = new EvidAuditUpdDataType();
        evidAuditUpdDataType.getAddrEvidUpdData().add(new AddrEvidUpdData1Type());
        evidAuditUpdDataType.getAddrEvidUpdData().get(0).setAuditDt("auditDt2");
        evidAuditUpdDataType.getAddrEvidUpdData().get(0).setAuditTm("auditTm2");

        evidAuditUpdDataType.getPartyEvidUpdData().add(new PartyEvidUpdData1Type());
        evidAuditUpdDataType.getPartyEvidUpdData().get(0).setAuditDt("auditDt3");
        evidAuditUpdDataType.getPartyEvidUpdData().get(0).setAuditTm("auditTm3");

        Collection auditDataList = auditDataFactory.createAudtitData(addressAuditUpdDataType, evidAuditUpdDataType);
        List<AuditData> auditDataList1 = (List<AuditData>) auditDataList;
        assertEquals("auditTm3", auditDataList1.get(0).getAuditTime());
        assertEquals("auditDt3", auditDataList1.get(0).getAuditDate());
        assertEquals("PARTY_EVIDENCE", auditDataList1.get(0).getAuditType());

        assertEquals("auditTm2", auditDataList1.get(1).getAuditTime());
        assertEquals("auditDt2", auditDataList1.get(1).getAuditDate());
        assertEquals("ADDRESS_EVIDENCE", auditDataList1.get(1).getAuditType());

        assertEquals("000123", auditDataList1.get(2).getAuditTime());
        assertEquals("00001234", auditDataList1.get(2).getAuditDate());
        assertEquals("ADDRESS", auditDataList1.get(2).getAuditType());


    }

    @Test
    public void testGetAuditDataWithEmptyLists() {

        AddressAuditUpdData1Type addressAuditUpdDataType = new AddressAuditUpdData1Type();
        addressAuditUpdDataType.setAuditTm(123L);
        addressAuditUpdDataType.setAuditDt(1234L);

        EvidAuditUpdDataType evidAuditUpdDataType = new EvidAuditUpdDataType();
        Collection auditDataList = auditDataFactory.createAudtitData(addressAuditUpdDataType, evidAuditUpdDataType);
        List<AuditData> auditDataList1 = (List<AuditData>) auditDataList;
        assertEquals("000123", auditDataList1.get(0).getAuditTime());
        assertEquals("00001234", auditDataList1.get(0).getAuditDate());
        assertEquals("ADDRESS", auditDataList1.get(0).getAuditType());


    }
}
