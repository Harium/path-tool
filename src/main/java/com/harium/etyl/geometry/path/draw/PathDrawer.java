package com.harium.etyl.geometry.path.draw;

import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.curve.Curve;

public interface PathDrawer {

    void drawLine(Graphics g, Point2D a, Point2D b);

    void drawCurve(Graphics g, Curve a);

    void drawCurve(Graphics g, Point2D[] list);

    void drawPoint(Graphics g, Point2D point);

    void drawLastPoint(Graphics g, Point2D point);

    void drawControlPoint(Graphics g, Point2D point);

    void setOffset(int x, int y);

    int getX();

    int getY();
}
