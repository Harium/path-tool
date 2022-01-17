package com.harium.etyl.geometry.path.svg.importer;

import com.harium.etyl.geometry.Path2D;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void testOptimizedPath() {
        String pathData = "m 66.245779,98.660037 c -0.670162,17.928703 13.800311,40.768673 35.734251,40.234503 21.93394,-0.53417 36.10017,-23.9415 36.10017,-40.269479 0,-16.327976 -8.55014,-41.14763 -35.37301,-40.743461 -26.82286,0.404169 -35.791249,22.849737 -36.461411,40.778437 z";
        List<Path2D> result = parser.parseData(pathData);

        assertEquals(4, result.get(0).getCurves().size());
    }
}
