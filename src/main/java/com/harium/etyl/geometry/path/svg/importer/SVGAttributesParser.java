package com.harium.etyl.geometry.path.svg.importer;

import com.harium.etyl.geometry.path.ElementAttributes;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;

public class SVGAttributesParser {

    public ElementAttributes parseAttributes(Attributes attributes) {
        ElementAttributes sa = new ElementAttributes();

        for (Attribute attr : attributes) {
            sa.set(attr.getKey(), attr.getValue());
        }

        return sa;
    }
}
