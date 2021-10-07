package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CarOwnerRecordTest {
    private static final String data = "21,28460,Single,Male,1,Zafira";
    private static final double delta = 1e-4;

    @Test
    public void testConstructTraining() {
        CarOwnerRecord record = new CarOwnerRecord(data, false);

        assertEquals(record.status, "Single");
        assertEquals(record.gender, "Male");
        assertEquals(record.model, "Zafira");

        // 以下属性经过了正则化，不能直接测试。
        // assertEquals(record.children, 1, delta);
        // assertEquals(record.age, 21, delta);
        // assertEquals(record.income, 28460, delta);
    }

    @Test
    public void testConstructTesting() {
        CarOwnerRecord record = new CarOwnerRecord(data, true);
        assertEquals(record.model, null);
        assertTrue(record.isTesting());
    }

}
