package com.harium.etyl.geometry.path.export;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SVGExporterTest {

    PathExporter exporter;

    @Before
    public void setUp() {
        exporter = new SVGExporter();
    }

    @Test
    public void testExportSegment() {
        Path2D path = new Path2D();
        path.add(new SegmentCurve(new Point2D(0, 0), new Point2D(10, 10)));
        path.add(new SegmentCurve(new Point2D(10, 10), new Point2D(20, 0)));
        path.add(new SegmentCurve(new Point2D(20, 0), new Point2D(30, 10)));

        String export = exporter.writeString(path);
        assertEquals("<svg width=\"30\" height=\"10\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                             + "  <path fill=\"transparent\" stroke=\"black\" d=\"M 0.0 0.0 L 10.0 10.0 L 20.0 0.0 L 30.0 10.0 \"/>\n"
                             + "</svg>", export);
    }

    @Test
    public void testExportQuadratic() {
        Path2D path = new Path2D();
        path.add(new QuadraticCurve(new Point2D(0, 0), new Point2D(10, 10), new Point2D(0, 10)));
        path.add(new QuadraticCurve(new Point2D(10, 10), new Point2D(20, 0), new Point2D(20, 10)));

        String export = exporter.writeString(path);
        assertEquals("<svg width=\"20\" height=\"10\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                             + "  <path fill=\"transparent\" stroke=\"black\" d=\"M 0.0 0.0 Q 0.0 10.0 10.0 10.0 Q 20.0 10.0 20.0 0.0 \"/>\n"
                             + "</svg>", export);
    }

}
