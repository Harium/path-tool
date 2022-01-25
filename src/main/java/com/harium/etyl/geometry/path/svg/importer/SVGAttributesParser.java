package com.harium.etyl.geometry.path.svg.importer;

import com.harium.etyl.geometry.path.ShapeAttributes;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;

public class SVGAttributesParser {

    public ShapeAttributes parseAttributes(Attributes attributes) {
        ShapeAttributes sa = new ShapeAttributes();

        for (Attribute attr : attributes) {
            sa.set(attr.getKey(), attr.getValue());
        }

        return sa;
    }
}
