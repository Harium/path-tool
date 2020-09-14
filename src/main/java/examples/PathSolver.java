package examples;

import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.event.PointerState;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.geometry.curve.CubicBezier;
import com.harium.etyl.geometry.curve.Curve;
import com.harium.etyl.geometry.curve.QuadraticBezier;
import com.harium.etyl.geometry.path.*;
import com.harium.etyl.geometry.path.draw.BasePathDrawer;
import com.harium.etyl.geometry.path.draw.PathDrawer;
import com.harium.etyl.geometry.path.fit.LeastSquareSolver;

public class PathSolver extends Etyl {

    public PathSolver() {
        super(800, 600);
    }

    public static void main(String[] args) {
        PathSolver app = new PathSolver();
        app.setTitle("Path Solver");
        app.init();
    }

    @Override
    public Application startApplication() {
        return new HelloWorld(w, h);
    }

    public class HelloWorld extends Application {

        private PathDrawer pathDrawer = new BasePathDrawer();

        LeastSquareSolver solver;
        private Path2D path = new Path2D();

        private Point2D mousePosition = new Point2D();

        public HelloWorld(int w, int h) {
            super(w, h);
        }

        @Override
        public void load() {
            Point2D[] samplePoints = new Point2D[4];
            samplePoints[0] = new Point2D(130, 300);
            samplePoints[1] = new Point2D(230, 270);
            samplePoints[2] = new Point2D(330, 330);
            samplePoints[3] = new Point2D(530, 300);

            solver = new LeastSquareSolver();
            path = solver.solve(samplePoints);
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);

            g.setColor(Color.CORN_FLOWER_BLUE);

            if (path.isEmpty()) {
                return;
            } else {
                g.setColor(Color.CORN_FLOWER_BLUE);

                if (path.size() >= 1) {
                    drawPath(g, path);

                    //Point2D lastPoint = path.getLastPoint();
                    //pathDrawer.drawPoint(g, lastPoint);
                    //pathDrawer.drawLine(g, lastPoint, mousePosition);
                }

                if (isBezier) {
                    //g.setColor(Color.RED);
                    pathDrawer.drawLastPoint(g, anchor);
                    pathDrawer.drawLine(g, anchor, cp1);
                    pathDrawer.drawControlPoint(g, cp1);
                    //g.setColor(Color.BLUE);
                    pathDrawer.drawLine(g, anchor, cp2);
                    pathDrawer.drawControlPoint(g, cp2);
                }
            }
        }

        boolean released = true;
        boolean isBezier = false;

        Point2D anchor = new Point2D();
        Point2D cp1 = new Point2D();
        Point2D cp2 = new Point2D();

        @Override
        public void updateMouse(PointerEvent event) {
            super.updateMouse(event);

            mousePosition.setLocation(event.getX(), event.getY());

            if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_LEFT)) {
                anchor = new Point2D(mousePosition);
                released = false;

            } else if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
                released = true;

                Point2D end = new Point2D(mousePosition);

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
                            path.add(new QuadraticCurve(anchor, mousePosition, new Point2D(cp1)));
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

                cp1 = new Point2D();
                cp2 = new Point2D();
                isBezier = false;
            }

            if (!released) {
                if (!isBezier && event.getState() == PointerState.DRAGGED) {
                    cp1 = new Point2D();
                    cp2 = new Point2D();
                    isBezier = true;

                    Point2D end = new Point2D(anchor);

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
                    cp1.setLocation(mousePosition.x, mousePosition.y);
                    cp2.setLocation(anchor.x * 2 - cp1.x, anchor.y * 2 - cp1.y);
                }
            }
        }

        @Override
        public void updateKeyboard(KeyEvent event) {
            super.updateKeyboard(event);

            if (event.isKeyUp(KeyEvent.VK_C)) {
                path.close();
            }
        }

        private void drawPath(Graphics g, Path2D path) {
            for (DataCurve c : path.getCurves()) {
                switch (c.getType()) {
                case SEGMENT:
                    //pathDrawer.drawLine(g, c.getData().getP0(), c.getData().getP1());
                    pathDrawer.drawCurve(g, c.getData());
                    pathDrawer.drawPoint(g, c.getStart());
                    pathDrawer.drawPoint(g, c.getEnd());
                    break;
                case QUADRATIC_BEZIER:
                    QuadraticBezier qCurve = (QuadraticBezier) c.getData();
                    pathDrawer.drawCurve(g, qCurve);

                    // Draw control points
                    pathDrawer.drawLine(g, qCurve.getP0(), qCurve.getP1());
                    pathDrawer.drawControlPoint(g, qCurve.getP1());
                    // Start
                    pathDrawer.drawPoint(g, qCurve.getP0());
                    // End
                    pathDrawer.drawPoint(g, qCurve.getP2());
                    break;
                case CUBIC_BEZIER:
                    CubicBezier cCurve = (CubicBezier) c.getData();
                    pathDrawer.drawCurve(g, cCurve);

                    // Draw control points
                    pathDrawer.drawLine(g, cCurve.getP2(), cCurve.getP3());
                    pathDrawer.drawLine(g, cCurve.getP0(), cCurve.getP1());

                    pathDrawer.drawControlPoint(g, cCurve.getP1());
                    pathDrawer.drawControlPoint(g, cCurve.getP2());
                    // Start
                    pathDrawer.drawPoint(g, cCurve.getP0());
                    // End
                    pathDrawer.drawPoint(g, cCurve.getP3());
                    break;
                }
            }
        }
    }
}