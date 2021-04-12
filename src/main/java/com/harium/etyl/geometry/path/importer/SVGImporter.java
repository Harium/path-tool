package com.harium.etyl.geometry.path.importer;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.CurveType;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SVGImporter implements PathImporter {

    private static final double EPSILON = 0.0001d;

    private static final String CLOSE = "z";
    private static final String CLOSE_UPPERCASE = "Z";
    private static final String MOVE = "M";
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

    @Override
    public List<Path2D> read(String data) {
        List<Path2D> list = new ArrayList<>();

        Document doc = Jsoup.parse(data);
        // Parse paths only
        Elements pathElements = doc.getElementsByTag("path");
        for (Element element: pathElements) {
            String d = element.attr("d");
            List<Path2D> paths = parseData(d);
            list.addAll(paths);
        }

        return list;
    }

    @Override
    public List<Path2D> read(File file) throws IOException {
        StringBuilder sb;
        BufferedReader buf = null;
        try {
            InputStream is = new FileInputStream(file);
            buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
        } finally {
            if (buf != null) {
                buf.close();
            }
        }

        String data = sb.toString();
        return read(data);
    }


    private List<Path2D> parseData(String d) {
        List<Path2D> paths = new ArrayList<>();

        String data = cleanData(d);
        String[] parts = data.split(" ");

        Path2D path = new Path2D();
        Point2D lastPoint = new Point2D();
        for (int i = 0; i < parts.length; i++) {
            if (MOVE.equals(parts[i])) {
                lastPoint = parseMove(parts, lastPoint, i);
                i+=2;
            } else if (LINE_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseLineAbsolute(parts, path, lastPoint, i);
                i+=2;
            } else if (LINE_RELATIVE.equals(parts[i])) {
                lastPoint = parseLineRelative(parts, path, lastPoint, i);
                i+=2;
            } else if (HORIZONTAL_LINE_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseHorizontalLineAbsolute(parts, path, lastPoint, i);
                i+=1;
            } else if (HORIZONTAL_LINE_RELATIVE.equals(parts[i])) {
                lastPoint = parseHorizontalLineRelative(parts, path, lastPoint, i);
                i+=1;
            } else if (VERTICAL_LINE_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseVerticalLineAbsolute(parts, path, lastPoint, i);
                i+=1;
            } else if (VERTICAL_LINE_RELATIVE.equals(parts[i])) {
                lastPoint = parseVerticalLineRelative(parts, path, lastPoint, i);
                i+=1;
            } else if (QUADRATIC_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseQuadraticAbsolute(parts, path, lastPoint, i);
                i+=4;
            } else if (QUADRATIC_RELATIVE.equals(parts[i])) {
                lastPoint = parseQuadraticRelative(parts, path, lastPoint, i);
                i+=4;
            } else if (CUBIC_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseCubicAbsolute(parts, path, lastPoint, i);
                i+=6;
            } else if (CUBIC_RELATIVE.equals(parts[i])) {
                lastPoint = parseCubicRelative(parts, path, lastPoint, i);
                i+=6;
            } else if (SHORTHAND_CUBIC_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseShorthandCubicAbsolute(parts, path, lastPoint, i);
                i+=4;
            } else if (SHORTHAND_CUBIC_RELATIVE.equals(parts[i])) {
                lastPoint = parseShorthandCubicRelative(parts, path, lastPoint, i);
                i+=4;
            } else if (CLOSE.equals(parts[i]) || CLOSE_UPPERCASE.equals(parts[i])) {
                lastPoint = parseClose(parts, path, lastPoint, i);
                paths.add(path);
                path = new Path2D();
            }
        }
        if (!path.isEmpty()) {
            paths.add(path);
        }
        return paths;
    }

    String cleanData(String d) {
        return d.replaceAll(MOVE, " " + MOVE + " ")
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

    private Point2D parseMove(String[] parts, Point2D lastPoint, int i) {
        double x = Double.parseDouble(parts[i + 1]);
        double y = Double.parseDouble(parts[i + 2]);

        lastPoint = new Point2D(x, y);
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
