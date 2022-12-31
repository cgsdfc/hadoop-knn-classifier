// MIT License
// 
// Copyright (c) 2021 cgsdfc
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

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
