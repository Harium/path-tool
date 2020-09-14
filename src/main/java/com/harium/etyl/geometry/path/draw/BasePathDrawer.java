package com.harium.etyl.geometry.path.draw;

import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.curve.Curve;

public class BasePathDrawer implements PathDrawer {

    private static final int POINT_SIZE = 6;
    private static final int HALF_POINT_SIZE = POINT_SIZE / 2;

    public void drawLine(Graphics g, Point2D a, Point2D b) {
        g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
    }

    public void drawCurve(Graphics g, Curve a) {
        Point2D[] v = a.flattenCurve(16);
        drawCurve(g, v);
    }

    public void drawCurve(Graphics g, Point2D[] list) {
        for (int i = 0; i < list.length - 1; i++) {
            Point2D v = list[i];
            Point2D n = list[i + 1];
            drawLine(g, v, n);
        }
    }

    public void drawPoint(Graphics g, Point2D point) {
        g.setColor(Color.WHITE);
        g.fillRect((int)(point.x - HALF_POINT_SIZE), (int)(point.y - HALF_POINT_SIZE), POINT_SIZE, POINT_SIZE);
        g.setColor(Color.CORN_FLOWER_BLUE);
        g.drawRect(point.x - HALF_POINT_SIZE, point.y - HALF_POINT_SIZE, POINT_SIZE, POINT_SIZE);
    }

    public void drawLastPoint(Graphics g, Point2D point) {
        g.fillRect((int)(point.x - HALF_POINT_SIZE), (int)(point.y - HALF_POINT_SIZE), POINT_SIZE, POINT_SIZE);
    }

    public void drawControlPoint(Graphics g, Point2D point) {
        g.fillCircle(point.x, point.y, HALF_POINT_SIZE);
    }

}
