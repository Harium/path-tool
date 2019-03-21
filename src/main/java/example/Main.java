package example;

import com.badlogic.gdx.math.Vector2;
import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.event.PointerState;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.curve.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Etyl {

    public Main() {
        super(800, 600);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setTitle("Path Maker");
        app.init();
    }

    @Override
    public Application startApplication() {
        return new HelloWorld(w, h);
    }

    public class HelloWorld extends Application {

        private static final int POINT_SIZE = 6;
        private static final int HALF_POINT_SIZE = POINT_SIZE / 2;

        List<CurvePath> paths = new ArrayList<>();
        // Current Path
        private CurvePath path = new CurvePath();

        private Vector2 mousePosition = new Vector2();

        public HelloWorld(int w, int h) {
            super(w, h);
        }

        @Override
        public void load() {
            CurvePath a = new CurvePath();
            CurvePath b = new CurvePath();
            CurvePath c = new CurvePath();

            a.add(new SegmentCurve(new Vector2(10, 50), new Vector2(60, 110)));
            b.add(new QuadraticCurve(new Vector2(90, 50), new Vector2(110, 80), new Vector2(150, 50)));
            c.add(new CubicCurve(new Vector2(180, 70), new Vector2(210, 100), new Vector2(250, 50), new Vector2(290, 110)));

            paths.add(a);
            paths.add(b);
            paths.add(c);

            path = new CurvePath();
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);

            g.setColor(Color.CORN_FLOWER_BLUE);
            for (CurvePath p : paths) {
                drawPath(g, p);
            }

            if (path.isEmpty()) {
                return;
            } else {
                g.setColor(Color.CORN_FLOWER_BLUE);

                if (path.size() >= 1) {
                    drawPath(g, path);

                    //Vector2 lastPoint = path.getLastPoint();
                    //drawPoint(g, lastPoint);
                    //drawLine(g, lastPoint, mousePosition);
                }

                if (isBezier) {
                    //g.setColor(Color.RED);
                    drawLastPoint(g, anchor);
                    drawLine(g, anchor, cp1);
                    drawControlPoint(g, cp1);
                    //g.setColor(Color.BLUE);
                    drawLine(g, anchor, cp2);
                    drawControlPoint(g, cp2);
                }
            }
        }

        boolean released = true;
        boolean isBezier = false;

        Vector2 anchor = new Vector2();
        Vector2 cp1 = new Vector2();
        Vector2 cp2 = new Vector2();

        @Override
        public void updateMouse(PointerEvent event) {
            super.updateMouse(event);

            mousePosition.set(event.getX(), event.getY());

            if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_LEFT)) {
                anchor = new Vector2(mousePosition);
                released = false;

            } else if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
                released = true;

                Vector2 end = new Vector2(mousePosition);

                if (!path.isEmpty()) {
                    DataCurve curve = path.lastCurve();

                    if (!isBezier) {
                        if (curve.getType() == CurveType.SEGMENT || curve.getType() == CurveType.QUADRATIC_BEZIER) {
                            curve.setEnd(end);
                            path.add(new SegmentCurve(end, mousePosition));
                        } else {
                            CubicCurve cubicCurve = (CubicCurve) curve;
                            cubicCurve.setEnd(end);
                            path.add(new QuadraticCurve(end, mousePosition, cubicCurve.getControl2()));
                        }
                    } else {
                        if (path.size() == 1) {
                            path.removeLast();
                            path.add(new QuadraticCurve(anchor, mousePosition, new Vector2(cp1)));
                        } else {
                            if (curve.getType() == CurveType.CUBIC_BEZIER) {
                                CubicCurve cubicCurve = (CubicCurve) curve;
                                path.add(new QuadraticCurve(curve.getEnd(), mousePosition, cubicCurve.getControl1()));
                            } else {
                                path.add(new SegmentCurve(curve.getEnd(), mousePosition));
                            }
                        }
                    }
                } else {
                    path.add(new SegmentCurve(end, mousePosition));
                }

                cp1 = new Vector2();
                cp2 = new Vector2();
                isBezier = false;
            }

            if (!released) {
                if (!isBezier && event.getState() == PointerState.DRAGGED) {
                    cp1 = new Vector2();
                    cp2 = new Vector2();
                    isBezier = true;

                    Vector2 end = new Vector2(anchor);

                    if (!path.isEmpty()) {
                        DataCurve lastCurve = path.lastCurve();
                        lastCurve.setEnd(end);

                        if (lastCurve.getType() == CurveType.SEGMENT) {
                            // Turn segment into Quadratic
                            path.removeLast();
                            path.add(new QuadraticCurve(lastCurve.getStart(), end, cp2));
                        } else if (lastCurve.getType() == CurveType.QUADRATIC_BEZIER) {
                            // Turn Quadratic into Cubic
                            QuadraticCurve quadraticCurve = (QuadraticCurve) lastCurve;
                            path.removeLast();
                            path.add(new CubicCurve(quadraticCurve.getStart(), end, quadraticCurve.getControl1(), cp2));
                        } else {
                            CubicCurve cubicCurve = (CubicCurve) lastCurve;
                            cubicCurve.setControl2(cp2);
                        }

                        path.add(new CubicCurve(lastCurve.getEnd(), end, cp1, cp2));
                    } else {
                        path.add(new CubicCurve(end, mousePosition, cp1, cp2));
                    }
                }

                if (isBezier) {
                    // TODO Add control points based on anchor position
                    cp1.set(mousePosition);
                    cp2.set(anchor.x * 2 - cp1.x, anchor.y * 2 - cp1.y);
                }
            }
        }

        @Override
        public void updateKeyboard(KeyEvent event) {
            super.updateKeyboard(event);
            if (event.isKeyUp(KeyEvent.VK_ESC)) {
                // Stop working with path
                path.removeLast();
                paths.add(path);
                path = new CurvePath();
            }

            if (event.isKeyUp(KeyEvent.VK_C)) {
                path.close();
            }
        }

        private void drawPath(Graphics g, CurvePath path) {
            for (DataCurve c : path.getCurves()) {
                switch (c.getType()) {
                    case SEGMENT:
                        //drawLine(g, c.getData().getP0(), c.getData().getP1());
                        drawCurve(g, c.getData());
                        drawPoint(g, c.getStart());
                        drawPoint(g, c.getEnd());
                        break;
                    case QUADRATIC_BEZIER:
                        QuadraticBezier qCurve = (QuadraticBezier) c.getData();
                        drawCurve(g, qCurve);

                        // Draw control points
                        drawLine(g, qCurve.getP0(), qCurve.getP1());
                        drawControlPoint(g, qCurve.getP1());
                        // Start
                        drawPoint(g, qCurve.getP0());
                        // End
                        drawPoint(g, qCurve.getP2());
                        break;
                    case CUBIC_BEZIER:
                        CubicBezier cCurve = (CubicBezier) c.getData();
                        drawCurve(g, cCurve);

                        // Draw control points
                        drawLine(g, cCurve.getP2(), cCurve.getP3());
                        drawLine(g, cCurve.getP0(), cCurve.getP1());

                        drawControlPoint(g, cCurve.getP1());
                        drawControlPoint(g, cCurve.getP2());
                        // Start
                        drawPoint(g, cCurve.getP0());
                        // End
                        drawPoint(g, cCurve.getP3());
                        break;
                }
            }
        }

        private void drawLine(Graphics g, Vector2 a, Vector2 b) {
            g.drawLine(a.x, a.y, b.x, b.y);
        }

        private void drawCurve(Graphics g, Curve a) {
            Vector2[] v = a.flattenCurve(16);
            drawCurve(g, v);
        }

        private void drawCurve(Graphics g, Vector2[] list) {
            for (int i = 0; i < list.length - 1; i++) {
                Vector2 v = list[i];
                Vector2 n = list[i + 1];
                g.drawLine(v.x, v.y, n.x, n.y);
            }
        }

        private void drawPoint(Graphics g, Vector2 point) {
            g.setColor(Color.WHITE);
            g.fillRect(point.x - HALF_POINT_SIZE, point.y - HALF_POINT_SIZE, POINT_SIZE, POINT_SIZE);
            g.setColor(Color.CORN_FLOWER_BLUE);
            g.drawRect(point.x - HALF_POINT_SIZE, point.y - HALF_POINT_SIZE, POINT_SIZE, POINT_SIZE);
        }

        private void drawLastPoint(Graphics g, Vector2 point) {
            g.fillRect(point.x - HALF_POINT_SIZE, point.y - HALF_POINT_SIZE, POINT_SIZE, POINT_SIZE);
        }

        private void drawControlPoint(Graphics g, Vector2 point) {
            g.fillCircle(point.x, point.y, HALF_POINT_SIZE);
        }
    }
}