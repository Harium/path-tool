package com.harium.etyl.geometry.curve;

import com.badlogic.gdx.math.Vector2;

public class SegmentCurve extends DataCurve {

    public SegmentCurve() {
        curve = new Curve();
    }

    public SegmentCurve(Vector2 start, Vector2 end) {
        this();
        curve.p0 = start;
        curve.p1 = end;
        this.start = start;
        this.end = end;
    }

    public Curve getData() {
        return curve;
    }
}
