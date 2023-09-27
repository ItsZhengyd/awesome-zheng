package org.example.suite;

import org.junit.Test;

import static org.junit.Assert.*;

public class CaseOneTest {

    @Test
    public void add() {
        CaseOne caseOne = new CaseOne();
        int result = caseOne.add(1, 2);
        assertEquals(3, result, 0);
    }
}