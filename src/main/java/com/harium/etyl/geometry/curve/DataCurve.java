package com.harium.etyl.geometry.curve;

import com.badlogic.gdx.math.Vector2;

import static com.harium.etyl.geometry.curve.CurveType.SEGMENT;

public abstract class DataCurve {

    public static final int SEGMENT_COUNT = 16;
    protected CurveType type = SEGMENT;

    protected Vector2 start;
    protected Vector2 end;

    protected Curve curve;

    protected Vector2[] segments;

    public DataCurve() {

    }

    public Vector2 getStart() {
        return start;
    }

    public void setStart(Vector2 start) {
        this.start = start;
        curve.p0 = start;
        updateCurve();
    }

    public Vector2 getEnd() {
        return end;
    }

    public void setEnd(Vector2 end) {
        this.end = end;
        curve.p1 = end;
        updateCurve();
    }

    public CurveType getType() {
        return type;
    }

    public Curve getData() {
        return curve;
    }

    protected void updateCurve() {
        segments = curve.flattenCurve(SEGMENT_COUNT);
    }
}
