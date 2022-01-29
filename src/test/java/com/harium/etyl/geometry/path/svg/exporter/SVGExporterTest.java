package com.harium.etyl.geometry.path.svg.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;
import com.harium.etyl.geometry.path.ElementAttributes;
import com.harium.etyl.geometry.path.exporter.PathExporter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
                             + "  <path d=\"M 0.0 0.0 L 10.0 10.0 L 20.0 0.0 L 30.0 10.0\"/>\n"
                             + "</svg>", export);
    }

    @Test
    public void testExportSegmentWithStyle() {
        Path2D path = new Path2D();
        path.add(new SegmentCurve(new Point2D(0, 0), new Point2D(10, 10)));
        path.add(new SegmentCurve(new Point2D(10, 10), new Point2D(20, 0)));
        path.add(new SegmentCurve(new Point2D(20, 0), new Point2D(30, 10)));

        ElementAttributes defaultStyle = new ElementAttributes();
        defaultStyle.id("my-path");
        defaultStyle.fill(ElementAttributes.COLOR_NONE);
        defaultStyle.stroke(ElementAttributes.COLOR_BLACK);
        defaultStyle.strokeWidth(ElementAttributes.DEFAULT_WIDTH);
        defaultStyle.strokeLineCap(ElementAttributes.DEFAULT_LINECAP);
        defaultStyle.strokeLineJoin(ElementAttributes.DEFAULT_LINEJOIN);
        defaultStyle.strokeOpacity(ElementAttributes.DEFAULT_OPACITY);

        String export = exporter.writeString(path, defaultStyle);
        assertEquals("<svg width=\"30\" height=\"10\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                             + "  <path id=\"my-path\" fill=\"none\" stroke=\"black\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-opacity=\"1\" d=\"M 0.0 0.0 L 10.0 10.0 L 20.0 0.0 L 30.0 10.0\"/>\n"
                             + "</svg>", export);
    }

    @Test
    public void testExportQuadratic() {
        Path2D path = new Path2D();
        path.add(new QuadraticCurve(new Point2D(0, 0), new Point2D(10, 10), new Point2D(0, 10)));
        path.add(new QuadraticCurve(new Point2D(10, 10), new Point2D(20, 0), new Point2D(20, 10)));

        String export = exporter.writeString(path);
        assertEquals("<svg width=\"20\" height=\"10\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                             + "  <path d=\"M 0.0 0.0 Q 0.0 10.0, 10.0 10.0 Q 20.0 10.0, 20.0 0.0\"/>\n"
                             + "</svg>", export);
    }

    @Test
    public void testExportMultipleSegmentsWithStyle() {
        Path2D path1 = new Path2D();
        path1.add(new SegmentCurve(new Point2D(0, 0), new Point2D(10, 10)));

        Path2D path2 = new Path2D();
        path2.add(new SegmentCurve(new Point2D(10, 10), new Point2D(20, 0)));

        Path2D path3 = new Path2D();
        path3.add(new SegmentCurve(new Point2D(20, 0), new Point2D(30, 10)));

        List<Path2D> paths = new ArrayList<>();
        paths.add(path1);
        paths.add(path2);
        paths.add(path3);

        ElementAttributes defaultStyle = new ElementAttributes();
        defaultStyle.fill(ElementAttributes.COLOR_NONE);
        defaultStyle.stroke(ElementAttributes.COLOR_BLACK);
        defaultStyle.strokeWidth(ElementAttributes.DEFAULT_WIDTH);
        defaultStyle.strokeLineCap(ElementAttributes.DEFAULT_LINECAP);
        defaultStyle.strokeLineJoin(ElementAttributes.DEFAULT_LINEJOIN);
        defaultStyle.strokeOpacity(ElementAttributes.DEFAULT_OPACITY);

        List<ElementAttributes> attributes = new ArrayList<>();
        attributes.add(defaultStyle);
        attributes.add(defaultStyle);
        attributes.add(defaultStyle);

        String export = exporter.writeString(paths, attributes);
        assertEquals("<svg width=\"30\" height=\"10\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                             + "  <path fill=\"none\" stroke=\"black\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-opacity=\"1\" d=\"M 0.0 0.0 L 10.0 10.0\"/>\n"
                             + "  <path fill=\"none\" stroke=\"black\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-opacity=\"1\" d=\"M 10.0 10.0 L 20.0 0.0\"/>\n"
                             + "  <path fill=\"none\" stroke=\"black\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-opacity=\"1\" d=\"M 20.0 0.0 L 30.0 10.0\"/>\n"
                             + "</svg>", export);
    }

}
