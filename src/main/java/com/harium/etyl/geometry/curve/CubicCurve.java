package com.harium.etyl.geometry.curve;

import com.badlogic.gdx.math.Vector2;

public class CubicCurve extends QuadraticCurve {

    protected Vector2 control2;

    public CubicCurve(Vector2 start, Vector2 end, Vector2 control1, Vector2 control2) {
        super(start, end, control1);
        type = CurveType.CUBIC_BEZIER;
        this.control2 = control2;

        curve = new CubicBezier(start, control1, control2, end);
        //updateCurve();
    }

    public void setEnd(Vector2 end) {
        this.end = end;
        ((CubicBezier) curve).p3 = end;
        updateCurve();
    }

    public void setControl2(Vector2 control2) {
        this.control2 = control2;
        ((CubicBezier) curve).p2 = control2;
        updateCurve();
    }

    public Vector2 getControl2() {
        return control2;
    }
}
