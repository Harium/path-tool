package com.harium.etyl.geometry.path;

import java.util.HashMap;
import java.util.Map;

public class ShapeAttributes {

    public static final String ATTR_ID = "id";
    public static final String ATTR_FILL = "fill";
    public static final String ATTR_STROKE_WIDTH = "stroke-width";
    public static final String ATTR_STROKE = "stroke";
    public static final String ATTR_STROKE_LINECAP = "stroke-linecap";
    public static final String ATTR_STROKE_LINEJOIN = "stroke-linejoin";
    public static final String ATTR_STROKE_OPACITY = "stroke-opacity";

    public static final String COLOR_NONE = "none";
    public static final String COLOR_BLACK = "black";
    public static final String DEFAULT_WIDTH = "1";
    public static final String DEFAULT_OPACITY = "1";
    public static final String DEFAULT_LINECAP = "butt";
    public static final String DEFAULT_LINEJOIN = "miter";

    private Map<String, String> attributes = new HashMap<>();

    public ShapeAttributes() {

    }

    public ShapeAttributes(ShapeAttributes attributes) {
        this.copy(attributes);
    }

    private void copy(ShapeAttributes attributes) {
        if (attributes.attributes == null) {
            return;
        }
        this.attributes.putAll(attributes.attributes);
    }

    public String get(String attribute) {
        return this.attributes.get(attribute);
    }

    public void set(String attribute, String value) {
        this.attributes.put(attribute, value);
    }

    public void setId(String id) {
        set(ATTR_ID, id);
    }

    public String getId() {
        return get(ATTR_ID);
    }

    public String getFill() {
        return get(ATTR_FILL);
    }

    public void setFill(String fill) {
        set(ATTR_FILL, fill);
    }

    public String getStroke() {
        return get(ATTR_STROKE);
    }

    public void setStroke(String stroke) {
        set(ATTR_STROKE, stroke);
    }

    public String getStrokeWidth() {
        return get(ATTR_STROKE_WIDTH);
    }

    public void setStrokeWidth(String strokeWidth) {
        set(ATTR_STROKE_WIDTH, strokeWidth);
    }

    public String getStrokeLineCap() {
        return get(ATTR_STROKE_LINECAP);
    }

    public void setStrokeLineCap(String strokeLineCap) {
        set(ATTR_STROKE_LINECAP, strokeLineCap);
    }

    public String getStrokeLineJoin() {
        return get(ATTR_STROKE_LINEJOIN);
    }

    public void setStrokeLineJoin(String strokeLineJoin) {
        set(ATTR_STROKE_LINEJOIN, strokeLineJoin);
    }

    public String getStrokeOpacity() {
        return get(ATTR_STROKE_OPACITY);
    }

    public void setStrokeOpacity(String strokeOpacity) {
        set(ATTR_STROKE_OPACITY, strokeOpacity);
    }
}
