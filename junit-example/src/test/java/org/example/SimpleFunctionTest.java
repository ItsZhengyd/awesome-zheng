package org.example;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 简单方法测试
 */
public class SimpleFunctionTest {

    @Test
    public void add() {
        SimpleFunction simpleFunction = new SimpleFunction();
        double result = simpleFunction.add(10, 50);
        assertEquals(60, result, 0);
    }
}