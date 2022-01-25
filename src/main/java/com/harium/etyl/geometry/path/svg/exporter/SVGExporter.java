package com.harium.etyl.geometry.path.svg.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.exporter.PathExporter;
import com.harium.etyl.geometry.path.ElementAttributes;

import java.util.Collections;
import java.util.List;

public class SVGExporter implements PathExporter {

    SVGPathExporter pathExporter = new SVGPathExporter();

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
    public String writeString(List<Path2D> paths, List<ElementAttributes> attributes) {
        StringBuilder builder = new StringBuilder();

        Point2D[] coordinates = calculateViewPort(paths);
        addHeader(builder, coordinates[0], coordinates[1]);

        for (int i = 0; i < paths.size(); i++) {
            Path2D path = paths.get(i);
            ElementAttributes attr = attributes.get(i);
            exportSinglePath(path, attr, builder, coordinates);
        }

        addFooter(builder);
        return builder.toString();
    }

    @Override
    public String writeString(Path2D path) {
        return writeString(path, new ElementAttributes());
    }

    @Override
    public String writeString(Path2D path, ElementAttributes pathOptions) {
        StringBuilder builder = new StringBuilder();

        Point2D[] coordinates = calculateViewPort(Collections.singletonList(path));
        addHeader(builder, coordinates[0], coordinates[1]);

        exportSinglePath(path, pathOptions, builder, coordinates);

        addFooter(builder);
        return builder.toString();
    }

    private void exportSinglePath(Path2D path, ElementAttributes shapeAttributes, StringBuilder builder, Point2D[] coordinates) {
        pathExporter.openPath(builder, shapeAttributes);
        Point2D offset = new Point2D(-coordinates[0].x, -coordinates[0].y);
        pathExporter.appendPath(builder, offset, path);
        pathExporter.closePath(builder);
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
