package com.harium.etyl.geometry.path.importer;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.CubicCurve;
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

    private static final String MOVE = "M";
    private static final String LINE_ABSOLUTE = "L";
    private static final String LINE_RELATIVE = "l";
    private static final String QUADRATIC_ABSOLUTE = "Q";
    private static final String QUADRATIC_RELATIVE = "q";
    private static final String CUBIC_ABSOLUTE = "C";
    private static final String CUBIC_RELATIVE = "c";

    @Override
    public List<Path2D> read(String data) {
        List<Path2D> list = new ArrayList<>();

        Document doc = Jsoup.parse(data);
        // Parse paths only
        Elements paths = doc.getElementsByTag("path");
        for (Element element: paths) {
            String d = element.attr("d");
            Path2D path = parseData(d);
            list.add(path);
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


    private Path2D parseData(String d) {
        String data = cleanData(d);
        String[] parts = data.split(" ");

        Path2D path = new Path2D();
        Point2D lastPoint = new Point2D();
        for (int i = 0; i < parts.length; i++) {
            if (MOVE.equals(parts[i])) {
                parseMove(parts, lastPoint, i);
                i+=2;
            } else if (LINE_ABSOLUTE.equals(parts[i])) {
                lastPoint = parseLineAbsolute(parts, path, lastPoint, i);
                i+=2;
            } else if (LINE_RELATIVE.equals(parts[i])) {
                lastPoint = parseLineRelative(parts, path, lastPoint, i);
                i+=2;
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
            }
        }

        return path;
    }

    String cleanData(String d) {
        return d.replaceAll(",", " ")
                .replaceAll(" +", " ")
                .trim();
    }

    private void parseMove(String[] parts, Point2D lastPoint, int cursor) {
        lastPoint.x = Double.parseDouble(parts[cursor + 1]);
        lastPoint.y = Double.parseDouble(parts[cursor + 2]);
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
        double dx = Double.parseDouble(parts[i + 1])+lastPoint.x;
        double dy = Double.parseDouble(parts[i + 2])+lastPoint.y;
        SegmentCurve curve = new SegmentCurve();
        curve.setStart(lastPoint);
        lastPoint = new Point2D(dx, dy);
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
}
