package com.harium.etyl.geometry.path.export;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;

public class SVGExporter implements PathExporter {

    @Override
    public String writeString(Path2D path) {
        StringBuilder builder = new StringBuilder();

        Point2D[] coordinates = calculateViewPort(path);
        addHeader(builder, coordinates[0], coordinates[1]);

        openPath(builder, path);
        appendCurves(builder, coordinates[0], path);
        closePath(builder);

        addFooter(builder);
        return builder.toString();
    }

    private void closePath(StringBuilder builder) {
        builder.append("/>\n");
    }

    private void appendCurves(StringBuilder builder, Point2D min, Path2D path) {
        boolean isClosed = false;

        builder.append("d=\"");

        appendFirstCurve(builder, min, path);

        for (DataCurve curve : path.getCurves()) {
            switch (curve.getType()) {
                case SEGMENT:
                    appendSegment(builder, min, (SegmentCurve) curve);
                    break;
                case QUADRATIC_BEZIER:
                    appendQuadratic(builder, min, (QuadraticCurve) curve);
                    break;
                case CUBIC_BEZIER:
                    appendCubic(builder, min, (CubicCurve) curve);
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
        builder.append(first.getStart().x - min.x);
        builder.append(" ");
        builder.append(first.getStart().y - min.y);
        builder.append(" ");
    }

    private void openPath(StringBuilder builder, Path2D path) {
        builder.append("\n  <path fill=\"transparent\" stroke=\"black\" ");
    }

    private void appendSegment(StringBuilder builder, Point2D min, SegmentCurve curve) {
        builder.append("L ");
        builder.append(curve.getEnd().x - min.x);
        builder.append(" ");
        builder.append(curve.getEnd().y - min.y);
        builder.append(" ");
    }

    private void appendQuadratic(StringBuilder builder, Point2D min, QuadraticCurve curve) {
        builder.append("Q ");
        // Control Point
        builder.append(curve.getControl1().x - min.x);
        builder.append(" ");
        builder.append(curve.getControl1().y - min.y);
        builder.append(" ");
        // End Point
        builder.append(curve.getEnd().x - min.x);
        builder.append(" ");
        builder.append(curve.getEnd().y - min.y);
        builder.append(" ");
    }

    private void appendCubic(StringBuilder builder, Point2D min, CubicCurve curve) {
        builder.append("C ");
        // Control Point 1
        builder.append(curve.getControl1().x - min.x);
        builder.append(" ");
        builder.append(curve.getControl1().y - min.y);
        builder.append(" ");
        // Control Point 2
        builder.append(curve.getControl2().x - min.x);
        builder.append(" ");
        builder.append(curve.getControl2().y - min.y);
        builder.append(" ");
        // End Point
        builder.append(curve.getEnd().x - min.x);
        builder.append(" ");
        builder.append(curve.getEnd().y - min.y);
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

    private Point2D[] calculateViewPort(Path2D path) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

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

        return new Point2D[]{new Point2D(minX, minY), new Point2D(maxX, maxY)};
    }

    private void addFooter(StringBuilder builder) {
        builder.append("</svg>");
    }
}
