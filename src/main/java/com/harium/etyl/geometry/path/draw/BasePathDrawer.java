package com.harium.etyl.geometry.path.draw;

import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.curve.Curve;

public class BasePathDrawer implements PathDrawer {

    private static final int POINT_SIZE = 6;
    private static final int HALF_POINT_SIZE = POINT_SIZE / 2;

    private int x = 0;
    private int y = 0;

    public void drawLine(Graphics g, Point2D a, Point2D b) {
        g.drawLine(x + (int) a.x, y + (int) a.y, x + (int) b.x, y + (int) b.y);
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
        g.fillRect(x + (int) (point.x - HALF_POINT_SIZE), y + (int) (point.y - HALF_POINT_SIZE), POINT_SIZE, POINT_SIZE);
        g.setColor(Color.CORN_FLOWER_BLUE);
        g.drawRect(x + point.x - HALF_POINT_SIZE, y + point.y - HALF_POINT_SIZE, POINT_SIZE, POINT_SIZE);
    }

    public void drawLastPoint(Graphics g, Point2D point) {
        g.fillRect(x + (int) (point.x - HALF_POINT_SIZE), y + (int) (point.y - HALF_POINT_SIZE), POINT_SIZE, POINT_SIZE);
    }

    public void drawControlPoint(Graphics g, Point2D point) {
        g.fillCircle(x + point.x, y + point.y, HALF_POINT_SIZE);
    }

    @Override
    public void setOffset(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

}
