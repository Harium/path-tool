package com.harium.etyl.geometry.path.svg.importer;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;
import org.junit.Before;
import org.junit.Test;
import utils.TestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SVGImporterTest {

    private static final double EPSILON = 0.0001f;

    SVGImporter importer;

    @Before
    public void setUp() {
        importer = new SVGImporter();
    }

    @Test
    public void testImportSegment() throws IOException {
        String data = TestUtils.loadResource("segment_path.svg");
        List<Path2D> paths = importer.read(data);
        assertEquals(1, paths.size());

        Path2D path = paths.get(0);
        assertEquals(3, path.getCurves().size());

        SegmentCurve curve1 = (SegmentCurve) path.getCurves().get(0);
        assertPoint(0, 0, curve1.getStart());
        assertPoint(10, 10, curve1.getEnd());

        SegmentCurve curve2 = (SegmentCurve) path.getCurves().get(1);
        assertPoint(10, 10, curve2.getStart());
        assertPoint(20, 0, curve2.getEnd());

        SegmentCurve curve3 = (SegmentCurve) path.getCurves().get(2);
        assertPoint(20, 0, curve3.getStart());
        assertPoint(30, 10, curve3.getEnd());
    }

    @Test
    public void testImportComplexPath() throws IOException {
        String data = TestUtils.loadResource("weird.svg");
        List<Path2D> paths = importer.read(data);
        assertEquals(1, paths.size());

        Path2D path = paths.get(0);
        assertEquals(12, path.getCurves().size());

        SegmentCurve curve1 = (SegmentCurve) path.getCurves().get(0);
        assertPoint(3, 83, curve1.getStart());
        assertPoint(115, 18, curve1.getEnd());

        SegmentCurve curve2 = (SegmentCurve) path.getCurves().get(1);
        assertPoint(115, 18, curve2.getStart());
        assertPoint(171, 139, curve2.getEnd());

        QuadraticCurve curve3 = (QuadraticCurve) path.getCurves().get(2);
        assertPoint(171, 139, curve3.getStart());
        assertPoint(336, 43, curve3.getEnd());
        assertPoint(331, 122, curve3.getControl1());

        CubicCurve curve4 = (CubicCurve) path.getCurves().get(3);
        assertPoint(336, 43, curve4.getStart());
        assertPoint(336, 43, curve4.getEnd());
        assertPoint(341, -36, curve4.getControl1());
        assertPoint(331, 122, curve4.getControl2());
    }

    private void assertPoint(double x, double y, Point2D point2D) {
        assertEquals(x, point2D.x, EPSILON);
        assertEquals(y, point2D.y, EPSILON);
    }

}
