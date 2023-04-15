package com.dissan.analyzer;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestFoo {

    public static int index = 1;
    @BeforeClass
    public static void setup(){
        index = 45;
    }

    @Test
    public void testInteger(){
        assertEquals(45 ,index);
    }
}
