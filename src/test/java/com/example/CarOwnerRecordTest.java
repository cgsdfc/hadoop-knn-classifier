package com.example;

import static org.junit.Assert.assertEquals;

import com.example.dataset.CarOwnersDataset.CarOwnerRecord;

import org.junit.Test;

public class CarOwnerRecordTest {
    private static final String data = "21,28460,Single,Male,1,Zafira";
    // private static final double delta = 1e-4;

    @Test
    public void testConstructor() {
        CarOwnerRecord record = new CarOwnerRecord(data);

        assertEquals(record.status, "Single");
        assertEquals(record.gender, "Male");
        assertEquals(record.model, "Zafira");

        // The following properties are regularized and cannot be tested directly.
        // assertEquals(record.children, 1, delta);
        // assertEquals(record.age, 21, delta);
        // assertEquals(record.income, 28460, delta);
    }

}
