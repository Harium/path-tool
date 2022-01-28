package com.harium.etyl.geometry.path.svg.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.exporter.PathExporter;
import com.harium.etyl.geometry.path.ElementAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SVGExporter implements PathExporter {

    private SVGPathExporter pathExporter = new SVGPathExporter();

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
        ElementAttributes attributes = new ElementAttributes().copy(shapeAttributes);

        Point2D offset = new Point2D(-coordinates[0].x, -coordinates[0].y);
        attributes.set("d", pathExporter.exportPath(offset, path));

        builder.append("\n  ");
        writeOnelinerTag("path", attributes, builder);
    }

    public void writeOnelinerTag(String tag, ElementAttributes attributes, StringBuilder builder) {
        builder.append("<");
        writeTag(tag, attributes, builder);
        builder.append("/>\n");
    }

    public void openTag(String tag, ElementAttributes attributes, StringBuilder builder) {
        builder.append("<");
        writeTag(tag, attributes, builder);
        builder.append(">");
    }

    private void writeTag(String tag, ElementAttributes attributes, StringBuilder builder) {
        builder.append(tag);
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.map.entrySet()) {
                appendAttribute(builder, entry.getKey(), entry.getValue());
            }
        }
    }

    private void appendAttribute(StringBuilder builder, String attribute, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        builder.append(" ");
        builder.append(attribute);
        builder.append("=\"");
        builder.append(value);
        builder.append("\"");
    }

    public void closeTag(String tag, StringBuilder builder) {
        builder.append("</");
        builder.append(tag);
        builder.append(">");
    }

    private void addHeader(StringBuilder builder, Point2D min, Point2D max) {
        int width = (int) (max.x - min.x);
        int height = (int) (max.y - min.y);

        ElementAttributes attributes = new ElementAttributes();
        attributes.set("width", Integer.toString(width));
        attributes.set("height", Integer.toString(height));
        attributes.set("xmlns", "http://www.w3.org/2000/svg");
        openTag("svg", attributes, builder);

    }

    private void addFooter(StringBuilder builder) {
        closeTag("svg", builder);
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
}
