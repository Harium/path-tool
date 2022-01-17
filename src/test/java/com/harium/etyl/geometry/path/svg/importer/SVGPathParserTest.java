package com.harium.etyl.geometry.path.svg.importer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SVGPathParserTest {

    private SVGPathParser parser;

    @Before
    public void setUp() {
        parser = new SVGPathParser();
    }

    @Test
    public void testCleanData() {
        assertEquals("1 2", parser.cleanData("1, 2"));
        assertEquals("3 4", parser.cleanData("3,  4"));
        assertEquals("1 2 3 4 5 6", parser.cleanData("1 2,  3 4 , 5 6"));
        assertEquals("1 2 3 4 5 6", parser.cleanData(" 1 2  ,  3 4 ,5  6 "));
    }
}
