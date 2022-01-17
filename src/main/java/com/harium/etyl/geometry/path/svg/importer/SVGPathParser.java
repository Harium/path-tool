package com.harium.etyl.geometry.path.svg.importer;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.CurveType;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;

import java.util.ArrayList;
import java.util.List;

public class SVGPathParser {

    private static final double EPSILON = 0.0001d;

    private static final String CLOSE = "z";
    private static final String CLOSE_UPPERCASE = "Z";
    private static final String MOVE_RELATIVE = "m";
    private static final String MOVE_ABSOLUTE = "M";
    private static final String LINE_ABSOLUTE = "L";
    private static final String LINE_RELATIVE = "l";
    private static final String HORIZONTAL_LINE_ABSOLUTE = "H";
    private static final String HORIZONTAL_LINE_RELATIVE = "h";
    private static final String VERTICAL_LINE_ABSOLUTE = "V";
    private static final String VERTICAL_LINE_RELATIVE = "v";
    private static final String QUADRATIC_ABSOLUTE = "Q";
    private static final String QUADRATIC_RELATIVE = "q";
    private static final String CUBIC_ABSOLUTE = "C";
    private static final String CUBIC_RELATIVE = "c";
    private static final String SHORTHAND_CUBIC_ABSOLUTE = "S";
    private static final String SHORTHAND_CUBIC_RELATIVE = "s";

    public List<Path2D> parseData(String d) {
        List<Path2D> paths = new ArrayList<>();

        String data = cleanData(d);
        String[] parts = data.split(" ");

        Path2D path = new Path2D();
        Point2D lastPoint = new Point2D();

        String lastCommand = MOVE_ABSOLUTE;

        for (int i = 0; i < parts.length; i++) {
            String currentPart = parts[i];
            if (!Character.isLetter(currentPart.charAt(0))) {
                currentPart = lastCommand;
                i--;
            }

            if (MOVE_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseMoveAbsolute(parts, lastPoint, i);
                i+=2;
            } else if (MOVE_RELATIVE.equals(currentPart)) {
                lastPoint = parseMoveRelative(parts, lastPoint, i);
                i+=2;
            } else if (LINE_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseLineAbsolute(parts, path, lastPoint, i);
                i+=2;
            } else if (LINE_RELATIVE.equals(currentPart)) {
                lastPoint = parseLineRelative(parts, path, lastPoint, i);
                i+=2;
            } else if (HORIZONTAL_LINE_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseHorizontalLineAbsolute(parts, path, lastPoint, i);
                i+=1;
            } else if (HORIZONTAL_LINE_RELATIVE.equals(currentPart)) {
                lastPoint = parseHorizontalLineRelative(parts, path, lastPoint, i);
                i+=1;
            } else if (VERTICAL_LINE_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseVerticalLineAbsolute(parts, path, lastPoint, i);
                i+=1;
            } else if (VERTICAL_LINE_RELATIVE.equals(currentPart)) {
                lastPoint = parseVerticalLineRelative(parts, path, lastPoint, i);
                i+=1;
            } else if (QUADRATIC_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseQuadraticAbsolute(parts, path, lastPoint, i);
                i+=4;
            } else if (QUADRATIC_RELATIVE.equals(currentPart)) {
                lastPoint = parseQuadraticRelative(parts, path, lastPoint, i);
                i+=4;
            } else if (CUBIC_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseCubicAbsolute(parts, path, lastPoint, i);
                i+=6;
            } else if (CUBIC_RELATIVE.equals(currentPart)) {
                lastPoint = parseCubicRelative(parts, path, lastPoint, i);
                i+=6;
            } else if (SHORTHAND_CUBIC_ABSOLUTE.equals(currentPart)) {
                lastPoint = parseShorthandCubicAbsolute(parts, path, lastPoint, i);
                i+=4;
            } else if (SHORTHAND_CUBIC_RELATIVE.equals(currentPart)) {
                lastPoint = parseShorthandCubicRelative(parts, path, lastPoint, i);
                i+=4;
            } else if (CLOSE.equals(currentPart) || CLOSE_UPPERCASE.equals(currentPart)) {
                lastPoint = parseClose(parts, path, lastPoint, i);
                paths.add(path);
                path = new Path2D();
            }
            lastCommand = currentPart;
        }
        if (!path.isEmpty()) {
            paths.add(path);
        }
        return paths;
    }

    String cleanData(String d) {
        return d.replaceAll(MOVE_ABSOLUTE, " " + MOVE_ABSOLUTE + " ")
                .replaceAll(MOVE_RELATIVE, " " + MOVE_RELATIVE + " ")
                .replaceAll(LINE_ABSOLUTE, " " + LINE_ABSOLUTE + " ")
                .replaceAll(LINE_RELATIVE, " " + LINE_RELATIVE + " ")
                .replaceAll(HORIZONTAL_LINE_ABSOLUTE, " " + HORIZONTAL_LINE_ABSOLUTE + " ")
                .replaceAll(HORIZONTAL_LINE_RELATIVE, " " + HORIZONTAL_LINE_RELATIVE + " ")
                .replaceAll(VERTICAL_LINE_ABSOLUTE, " " + VERTICAL_LINE_ABSOLUTE + " ")
                .replaceAll(VERTICAL_LINE_RELATIVE, " " + VERTICAL_LINE_RELATIVE + " ")
                .replaceAll(QUADRATIC_ABSOLUTE, " " + QUADRATIC_ABSOLUTE + " ")
                .replaceAll(QUADRATIC_RELATIVE, " " + QUADRATIC_RELATIVE + " ")
                .replaceAll(CUBIC_ABSOLUTE, " " + CUBIC_ABSOLUTE + " ")
                .replaceAll(CUBIC_RELATIVE, " " + CUBIC_RELATIVE + " ")
                .replaceAll(SHORTHAND_CUBIC_ABSOLUTE, " " + SHORTHAND_CUBIC_ABSOLUTE + " ")
                .replaceAll(SHORTHAND_CUBIC_RELATIVE, " " + SHORTHAND_CUBIC_RELATIVE + " ")
                .replaceAll(CLOSE, " " + CLOSE + " ")
                .replaceAll(CLOSE_UPPERCASE, " " + CLOSE_UPPERCASE + " ")
                .replaceAll("-", " -")
                .replaceAll(",", " ")
                .replaceAll(" +", " ")
                .trim();
    }

    private Point2D parseMoveAbsolute(String[] parts, Point2D lastPoint, int i) {
        double x = Double.parseDouble(parts[i + 1]);
        double y = Double.parseDouble(parts[i + 2]);

        lastPoint = new Point2D(x, y);
        return lastPoint;
    }

    private Point2D parseMoveRelative(String[] parts, Point2D lastPoint, int i) {
        double x = Double.parseDouble(parts[i + 1]);
        double y = Double.parseDouble(parts[i + 2]);

        lastPoint = new Point2D(lastPoint.x + x, lastPoint.y + y);
        return lastPoint;
    }

    private Point2D parseLineAbsolute(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double x = Double.parseDouble(parts[i + 1]);
        double y = Double.parseDouble(parts[i + 2]);
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(x, y);
        curve.setEnd(lastPoint);
        path.add(curve);
        return lastPoint;
    }

    private Point2D parseLineRelative(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double dx = Double.parseDouble(parts[i + 1]) + lastPoint.x;
        double dy = Double.parseDouble(parts[i + 2]) + lastPoint.y;
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(dx, dy);
        curve.setEnd(lastPoint);
        path.add(curve);
        return lastPoint;
    }

    private Point2D parseHorizontalLineAbsolute(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double x = Double.parseDouble(parts[i + 1]);
        double y = lastPoint.y;
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(x, y);
        curve.setEnd(lastPoint);
        path.add(curve);
        return lastPoint;
    }

    private Point2D parseHorizontalLineRelative(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double x = Double.parseDouble(parts[i + 1]) + lastPoint.x;
        double y = lastPoint.y;
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(x, y);
        curve.setEnd(lastPoint);
        path.add(curve);
        return lastPoint;
    }

    private Point2D parseVerticalLineAbsolute(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double x = lastPoint.x;
        double y = Double.parseDouble(parts[i + 1]);
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(x, y);
        curve.setEnd(lastPoint);
        path.add(curve);
        return lastPoint;
    }

    private Point2D parseVerticalLineRelative(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double x = lastPoint.x;
        double y = Double.parseDouble(parts[i + 1]) + lastPoint.y;
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(x, y);
        curve.setEnd(lastPoint);
        path.add(curve);
        return lastPoint;
    }

    private Point2D parseQuadraticAbsolute(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double c1x = Double.parseDouble(parts[i + 1]);
        double c1y = Double.parseDouble(parts[i + 2]);
        double x = Double.parseDouble(parts[i + 3]);
        double y = Double.parseDouble(parts[i + 4]);

        Point2D controlPoint = new Point2D(c1x, c1y);
        Point2D endPoint = new Point2D(x, y);

        QuadraticCurve curve = new QuadraticCurve(lastPoint, endPoint, controlPoint);

        path.add(curve);
        return endPoint;
    }

    private Point2D parseQuadraticRelative(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double dc1x = Double.parseDouble(parts[i + 1]) + lastPoint.x;
        double dc1y = Double.parseDouble(parts[i + 2]) + lastPoint.y;
        double dx = Double.parseDouble(parts[i + 3]) + lastPoint.x;
        double dy = Double.parseDouble(parts[i + 4]) + lastPoint.y;

        Point2D controlPoint = new Point2D(dc1x, dc1y);
        Point2D endPoint = new Point2D(dx, dy);

        QuadraticCurve curve = new QuadraticCurve(lastPoint, endPoint, controlPoint);

        path.add(curve);
        return endPoint;
    }

    private Point2D parseCubicAbsolute(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double c1x = Double.parseDouble(parts[i + 1]);
        double c1y = Double.parseDouble(parts[i + 2]);
        double c2x = Double.parseDouble(parts[i + 3]);
        double c2y = Double.parseDouble(parts[i + 4]);
        double x = Double.parseDouble(parts[i + 5]);
        double y = Double.parseDouble(parts[i + 6]);

        Point2D controlPoint1 = new Point2D(c1x, c1y);
        Point2D controlPoint2 = new Point2D(c2x, c2y);
        Point2D endPoint = new Point2D(x, y);

        CubicCurve curve = new CubicCurve(lastPoint, endPoint, controlPoint1, controlPoint2);

        path.add(curve);
        return endPoint;
    }

    private Point2D parseCubicRelative(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double dc1x = Double.parseDouble(parts[i + 1]) + lastPoint.x;
        double dc1y = Double.parseDouble(parts[i + 2]) + lastPoint.y;
        double dc2x = Double.parseDouble(parts[i + 3]) + lastPoint.x;
        double dc2y = Double.parseDouble(parts[i + 4]) + lastPoint.y;
        double dx = Double.parseDouble(parts[i + 5]) + lastPoint.x;
        double dy = Double.parseDouble(parts[i + 6]) + lastPoint.y;

        Point2D controlPoint1 = new Point2D(dc1x, dc1y);
        Point2D controlPoint2 = new Point2D(dc2x, dc2y);
        Point2D endPoint = new Point2D(dx, dy);

        CubicCurve curve = new CubicCurve(lastPoint, endPoint, controlPoint1, controlPoint2);

        path.add(curve);
        return endPoint;
    }

    private Point2D parseShorthandCubicAbsolute(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double c2x = Double.parseDouble(parts[i + 1]);
        double c2y = Double.parseDouble(parts[i + 2]);
        double x = Double.parseDouble(parts[i + 3]);
        double y = Double.parseDouble(parts[i + 4]);

        Point2D controlPoint2 = new Point2D(c2x, c2y);
        Point2D endPoint = new Point2D(x, y);
        if (CurveType.CUBIC_BEZIER != path.lastCurve().getType()) {
            // TODO throw exception
        }

        CubicCurve lastCurve = (CubicCurve) path.lastCurve();
        Point2D controlPoint1 = lastCurve.getControl2();

        CubicCurve curve = new CubicCurve(lastPoint, endPoint, controlPoint1, controlPoint2);

        path.add(curve);
        return endPoint;
    }

    private Point2D parseShorthandCubicRelative(String[] parts, Path2D path, Point2D lastPoint, int i) {
        double c2x = Double.parseDouble(parts[i + 1]) + lastPoint.x;
        double c2y = Double.parseDouble(parts[i + 2]) + lastPoint.y;
        double x = Double.parseDouble(parts[i + 3]) + lastPoint.x;
        double y = Double.parseDouble(parts[i + 4]) + lastPoint.y;

        Point2D controlPoint2 = new Point2D(c2x, c2y);
        Point2D endPoint = new Point2D(x, y);
        if (CurveType.CUBIC_BEZIER != path.lastCurve().getType()) {
            // TODO throw exception
        }

        CubicCurve lastCurve = (CubicCurve) path.lastCurve();
        Point2D controlPoint1 = lastCurve.getControl2();

        CubicCurve curve = new CubicCurve(lastPoint, endPoint, controlPoint1, controlPoint2);

        path.add(curve);
        return endPoint;
    }

    private Point2D parseClose(String[] parts, Path2D path, Point2D lastPoint, int i) {
        DataCurve lastCurve = path.lastCurve();
        DataCurve firstCurve = path.getCurves().get(0);

        double x1 = lastCurve.getEnd().x;
        double x2 = firstCurve.getEnd().x;
        double y1 = lastCurve.getEnd().y;
        double y2 = firstCurve.getEnd().y;

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (Math.abs(dx) < EPSILON && Math.abs(dy) < EPSILON) {
            // Draw a line
            SegmentCurve curve = new SegmentCurve();
            curve.setStart(lastPoint);
            curve.setEnd(firstCurve.getStart());
            path.add(curve);
        }

        return lastPoint;
    }

}
