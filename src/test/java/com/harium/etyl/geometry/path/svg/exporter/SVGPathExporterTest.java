package com.harium.etyl.geometry.path.svg.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.SegmentCurve;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SVGPathExporterTest {

    SVGPathExporter exporter;

    @Before
    public void setUp() {
        exporter = new SVGPathExporter();
    }

    @Test
    public void testExportSegment() {
        Path2D path = new Path2D();
        path.add(new SegmentCurve(new Point2D(0, 0), new Point2D(10, 10)));
        path.add(new SegmentCurve(new Point2D(10, 10), new Point2D(20, 0)));
        path.add(new SegmentCurve(new Point2D(20, 0), new Point2D(30, 10)));

        StringBuilder builder = new StringBuilder();
        exporter.appendPath(builder, path);

        assertEquals("d=\"M 0.0 0.0 L 10.0 10.0 L 20.0 0.0 L 30.0 10.0 \"", builder.toString());
    }

    @Test
    public void testExportClosedPath() {
        Path2D path = new Path2D();
        path.add(new SegmentCurve(new Point2D(0, 0), new Point2D(10, 0)));
        path.add(new SegmentCurve(new Point2D(10, 0), new Point2D(5, 5)));
        path.add(new SegmentCurve(new Point2D(5, 5), new Point2D(0, 0)));


        StringBuilder builder = new StringBuilder();
        exporter.appendPath(builder, path);

        assertEquals("d=\"M 0.0 0.0 L 10.0 0.0 L 5.0 5.0 L 0.0 0.0 Z\"", builder.toString());
    }

}
