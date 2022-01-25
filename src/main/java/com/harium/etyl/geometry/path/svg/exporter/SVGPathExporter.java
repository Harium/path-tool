package com.harium.etyl.geometry.path.svg.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;

import java.util.List;

public class SVGPathExporter {

    public void appendPath(StringBuilder builder, Path2D path) {
        appendPath(builder, new Point2D(0, 0), path);
    }

    public void appendPath(StringBuilder builder, Point2D offset, Path2D path) {
        builder.append("d=\"");
        appendCurves(builder, offset, path);
        builder.append("\"");
    }

    public void appendPaths(StringBuilder builder, List<Path2D> paths) {
        appendPaths(builder, new Point2D(0, 0), paths);
    }

    public void appendPaths(StringBuilder builder, Point2D offset, List<Path2D> paths) {
        builder.append("d=\"");
        for (int i = 0; i < paths.size(); i++) {
            Path2D path = paths.get(i);
            appendCurves(builder, offset, path);
            if (i < paths.size() - 2) {
                builder.append(" ");
            }
        }
        builder.append("\"");
    }

    private void appendCurves(StringBuilder builder, Point2D offset, Path2D path) {
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
        if (path.isClosed()) {
            builder.append("Z");
        }
    }

    private void appendFirstCurve(StringBuilder builder, Point2D offset, Path2D path) {
        DataCurve first = path.getCurves().get(0);

        builder.append("M ");
        builder.append(first.getStart().x + offset.x);
        builder.append(" ");
        builder.append(first.getStart().y + offset.y);
        builder.append(" ");
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

}
