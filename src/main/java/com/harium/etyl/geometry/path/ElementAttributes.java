package com.harium.etyl.geometry.path;

import java.util.HashMap;
import java.util.Map;

public class ElementAttributes {

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

    public ElementAttributes() {

    }

    public ElementAttributes(ElementAttributes attributes) {
        this.copy(attributes);
    }

    public ElementAttributes copy(ElementAttributes attributes) {
        if (attributes.attributes == null) {
            return this;
        }
        this.attributes.putAll(attributes.attributes);
        return this;
    }

    public String get(String attribute) {
        String value = this.attributes.get(attribute);
        if (value == null) {
            return "";
        }
        return value;
    }

    public void set(String attribute, String value) {
        this.attributes.put(attribute, value);
    }

    public ElementAttributes id(String id) {
        set(ATTR_ID, id);
        return this;
    }

    public String getId() {
        return get(ATTR_ID);
    }

    public String getFill() {
        return get(ATTR_FILL);
    }

    public ElementAttributes fill(String fill) {
        set(ATTR_FILL, fill);
        return this;
    }

    public String getStroke() {
        return get(ATTR_STROKE);
    }

    public ElementAttributes stroke(String stroke) {
        set(ATTR_STROKE, stroke);
        return this;
    }

    public String getStrokeWidth() {
        return get(ATTR_STROKE_WIDTH);
    }

    public ElementAttributes strokeWidth(String strokeWidth) {
        set(ATTR_STROKE_WIDTH, strokeWidth);
        return this;
    }

    public String getStrokeLineCap() {
        return get(ATTR_STROKE_LINECAP);
    }

    public ElementAttributes strokeLineCap(String strokeLineCap) {
        set(ATTR_STROKE_LINECAP, strokeLineCap);
        return this;
    }

    public String getStrokeLineJoin() {
        return get(ATTR_STROKE_LINEJOIN);
    }

    public ElementAttributes strokeLineJoin(String strokeLineJoin) {
        set(ATTR_STROKE_LINEJOIN, strokeLineJoin);
        return this;
    }

    public String getStrokeOpacity() {
        return get(ATTR_STROKE_OPACITY);
    }

    public ElementAttributes strokeOpacity(String strokeOpacity) {
        set(ATTR_STROKE_OPACITY, strokeOpacity);
        return this;
    }
}
