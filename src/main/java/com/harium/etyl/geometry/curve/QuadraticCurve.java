package com.harium.etyl.geometry.curve;

import com.badlogic.gdx.math.Vector2;

public class QuadraticCurve extends DataCurve {

    protected Vector2 control1;

    public QuadraticCurve(Vector2 start, Vector2 end, Vector2 control1) {
        type = CurveType.QUADRATIC_BEZIER;

        this.start = start;
        this.end = end;
        this.control1 = control1;

        curve = new QuadraticBezier(start, control1, end);
        //updateCurve();
    }

    public void setEnd(Vector2 end) {
        this.end = end;
        ((QuadraticBezier) curve).p2 = end;
        updateCurve();
    }

    public void setControl1(Vector2 control1) {
        this.control1 = control1;
        ((QuadraticBezier) curve).p1 = control1;
        updateCurve();
    }

    public Vector2 getControl1() {
        return control1;
    }
}
