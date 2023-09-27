package org.example.suite;

import org.junit.Test;

import static org.junit.Assert.*;

public class CaseTwoTest {

    @Test
    public void add() {
        CaseOne caseOne = new CaseOne();
        int result = caseOne.add(2, 2);
        assertEquals(4, result, 0);
    }
}