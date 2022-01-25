package com.harium.etyl.geometry.path.svg.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;
import com.harium.etyl.geometry.path.exporter.PathExporter;
import com.harium.etyl.geometry.path.ShapeAttributes;

import java.util.Collections;
import java.util.List;

public class SVGExporter implements PathExporter {

    @Override
    public String writeString(List<Path2D> paths) {
        StringBuilder builder = new StringBuilder();

        Point2D[] coordinates = calculateViewPort(paths);
        addHeader(builder, coordinates[0], coordinates[1]);

        for (Path2D path : paths) {
            exportSinglePath(path, null, builder, coordinates);
        }

        addFooter(builder);
        return builder.toString();
    }

    @Override
    public String writeString(List<Path2D> paths, List<ShapeAttributes> attributes) {
        StringBuilder builder = new StringBuilder();

        Point2D[] coordinates = calculateViewPort(paths);
        addHeader(builder, coordinates[0], coordinates[1]);

        for (int i = 0; i < paths.size(); i++) {
            Path2D path = paths.get(i);
            ShapeAttributes attr = attributes.get(i);
            exportSinglePath(path, attr, builder, coordinates);
        }

        addFooter(builder);
        return builder.toString();
    }

    @Override
    public String writeString(Path2D path) {
        return writeString(path, new ShapeAttributes());
    }

    @Override
    public String writeString(Path2D path, ShapeAttributes pathOptions) {
        StringBuilder builder = new StringBuilder();

        Point2D[] coordinates = calculateViewPort(Collections.singletonList(path));
        addHeader(builder, coordinates[0], coordinates[1]);

        exportSinglePath(path, pathOptions, builder, coordinates);

        addFooter(builder);
        return builder.toString();
    }

    private void exportSinglePath(Path2D path, ShapeAttributes shapeAttributes, StringBuilder builder, Point2D[] coordinates) {
        openPath(builder, shapeAttributes, path);
        Point2D offset = new Point2D(-coordinates[0].x, -coordinates[0].y);
        appendCurves(builder, offset, path);
        closePath(builder);
    }

    private void closePath(StringBuilder builder) {
        builder.append("/>");
    }

    private void appendCurves(StringBuilder builder, Point2D offset, Path2D path) {
        boolean isClosed = false;

        builder.append("d=\"");

        appendFirstCurve(builder, offset, path);

        for (DataCurve curve : path.getCurves()) {
            switch (curve.getType()) {
            case SEGMENT:
                appendSegment(builder, offset, (SegmentCurve) curve);
                break;
            case QUADRATIC_BEZIER:
                appendQuadratic(builder, offset, (QuadraticCurve) curve);
                break;
            case CUBIC_BEZIER:
                appendCubic(builder, offset, (CubicCurve) curve);
                break;
            default:
                System.err.println("Type " + curve.getType() + " not handled");
                break;
            }
        }
        if (isClosed) {
            builder.append("Z");
        }
        builder.append("\"");
    }

    private void appendFirstCurve(StringBuilder builder, Point2D min, Path2D path) {
        DataCurve first = path.getCurves().get(0);

        builder.append("M ");
        builder.append(first.getStart().x + min.x);
        builder.append(" ");
        builder.append(first.getStart().y + min.y);
        builder.append(" ");
    }

    private void openPath(StringBuilder builder, ShapeAttributes attributes, Path2D path) {
        builder.append("\n  <path");
        if (attributes != null) {
            appendStyleAttr(builder, "id", attributes);
            appendStyleAttr(builder, "fill", attributes);
            appendStyleAttr(builder, "stroke", attributes);
            appendStyleAttr(builder, "stroke-width", attributes);
            appendStyleAttr(builder, "stroke-linecap", attributes);
            appendStyleAttr(builder, "stroke-linejoin", attributes);
            appendStyleAttr(builder, "stroke-opacity", attributes);
        }
        builder.append(" ");
    }

    private void appendStyleAttr(StringBuilder builder, String attribute, ShapeAttributes attributeMap) {
        String value = attributeMap.get(attribute);
        if (value == null || value.isEmpty()) {
            return;
        }

        builder.append(" ");
        builder.append(attribute);
        builder.append("=\"");
        builder.append(value);
        builder.append("\"");
    }

    private void appendSegment(StringBuilder builder, Point2D offset, SegmentCurve curve) {
        builder.append("L ");
        builder.append(curve.getEnd().x + offset.x);
        builder.append(" ");
        builder.append(curve.getEnd().y + offset.y);
        builder.append(" ");
    }

    private void appendQuadratic(StringBuilder builder, Point2D offset, QuadraticCurve curve) {
        builder.append("Q ");
        // Control Point
        builder.append(curve.getControl1().x + offset.x);
        builder.append(" ");
        builder.append(curve.getControl1().y + offset.y);
        builder.append(", ");
        // End Point
        builder.append(curve.getEnd().x + offset.x);
        builder.append(" ");
        builder.append(curve.getEnd().y + offset.y);
        builder.append(" ");
    }

    private void appendCubic(StringBuilder builder, Point2D offset, CubicCurve curve) {
        builder.append("C ");
        // Control Point 1
        builder.append(curve.getControl1().x + offset.x);
        builder.append(" ");
        builder.append(curve.getControl1().y + offset.y);
        builder.append(", ");
        // Control Point 2
        builder.append(curve.getControl2().x + offset.x);
        builder.append(" ");
        builder.append(curve.getControl2().y + offset.y);
        builder.append(", ");
        // End Point
        builder.append(curve.getEnd().x + offset.x);
        builder.append(" ");
        builder.append(curve.getEnd().y + offset.y);
        builder.append(" ");
    }

    private void addHeader(StringBuilder builder, Point2D min, Point2D max) {
        int width = (int) (max.x - min.x);
        int height = (int) (max.y - min.y);

        builder.append("<svg width=\"");
        builder.append(width);
        builder.append("\" height=\"");
        builder.append(height);
        builder.append("\" xmlns=\"http://www.w3.org/2000/svg\">");
    }

    private Point2D[] calculateViewPort(List<Path2D> paths) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Path2D path : paths) {
            for (DataCurve curve : path.getCurves()) {
                Point2D start = curve.getStart();
                Point2D end = curve.getEnd();
                // Start
                if (start.x < minX) {
                    minX = (int) start.x;
                }
                if (start.x > maxX) {
                    maxX = (int) start.x;
                }
                if (start.y < minY) {
                    minY = (int) start.y;
                }
                if (start.y > maxY) {
                    maxY = (int) start.y;
                }
                // End
                if (end.x < minX) {
                    minX = (int) end.x;
                }
                if (end.x > maxX) {
                    maxX = (int) end.x;
                }
                if (end.y < minY) {
                    minY = (int) end.y;
                }
                if (end.y > maxY) {
                    maxY = (int) end.y;
                }
            }
        }

        return new Point2D[] { new Point2D(minX, minY), new Point2D(maxX, maxY) };
    }

    private void addFooter(StringBuilder builder) {
        builder.append("\n</svg>");
    }
}
