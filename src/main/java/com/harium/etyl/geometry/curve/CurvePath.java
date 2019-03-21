package com.harium.etyl.geometry.curve;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class CurvePath {

    Vector2 lastPoint = new Vector2();

    //List<Vector2> points = new ArrayList<>();
    List<DataCurve> curves = new ArrayList<>();

    public CurvePath() {
        super();
    }

    public void add(DataCurve curve) {
        curves.add(curve);
    }

    public boolean isEmpty() {
        return curves.isEmpty();
    }

    public int size() {
        return curves.size();
    }

    public List<DataCurve> getCurves() {
        return curves;
    }

    public void removeLast() {
        curves.remove(curves.size() - 1);
    }

    public DataCurve lastCurve() {
        return curves.get(curves.size() - 1);
    }

    public void close() {
        if (curves.size() < 2) {
            return;
        }
        DataCurve first = curves.get(0);
        DataCurve last = lastCurve();

        if (last.type == CurveType.SEGMENT) {
            last.setEnd(first.getStart());
        } else if (last.type == CurveType.QUADRATIC_BEZIER) {
            last.setEnd(first.getStart());
        } else {
            // TODO Calculate control point 2?
        }
    }

}
