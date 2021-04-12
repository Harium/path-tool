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
import com.harium.etyl.geometry.curve.QuadraticBezier;
import com.harium.etyl.geometry.path.CubicCurve;
import com.harium.etyl.geometry.path.CurveType;
import com.harium.etyl.geometry.path.DataCurve;
import com.harium.etyl.geometry.path.QuadraticCurve;
import com.harium.etyl.geometry.path.SegmentCurve;
import com.harium.etyl.geometry.path.draw.BasePathDrawer;
import com.harium.etyl.geometry.path.draw.PathDrawer;
import com.harium.etyl.geometry.path.exporter.PathExporter;
import com.harium.etyl.geometry.path.exporter.SVGExporter;
import com.harium.etyl.geometry.path.importer.PathImporter;
import com.harium.etyl.geometry.path.importer.SVGImporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathTool extends Etyl {

    private static final boolean DRAW_CONTROL_POINTS = true;

    public PathTool() {
        super(800, 600);
    }

    public static void main(String[] args) {
        PathTool app = new PathTool();
        app.setTitle("Path Maker");
        app.init();
    }

    @Override
    public Application startApplication() {
        return new PathToolApplication(w, h);
    }

    public class PathToolApplication extends Application {

        private PathImporter pathImporter = new SVGImporter();
        private PathExporter pathExporter = new SVGExporter();
        private PathDrawer pathDrawer = new BasePathDrawer();

        private List<Path2D> paths = new ArrayList<>();
        // Current Path
        private Path2D path = new Path2D();

        private Point2D mousePosition = new Point2D();

        // Control variables
        private boolean keyCtrl = false;
        private boolean released = true;
        private boolean isBezier = false;

        private boolean mouseMiddle = false;
        private int dragX, dragY;

        private Point2D anchor = new Point2D();
        private Point2D cp1 = new Point2D();
        private Point2D cp2 = new Point2D();

        public PathToolApplication(int w, int h) {
            super(w, h);
        }

        @Override
        public void load() {
            path = new Path2D();
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);

            g.setColor(Color.CORN_FLOWER_BLUE);
            for (Path2D p : paths) {
                drawPath(g, p);
            }

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

        @Override
        public void updateMouse(PointerEvent event) {
            super.updateMouse(event);

            mousePosition.setLocation(event.getX(), event.getY());

            if (!mouseMiddle) {
                if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_MIDDLE)) {
                    mouseMiddle = true;
                    dragX = event.getX() - pathDrawer.getX();
                    dragY = event.getY() - pathDrawer.getY();
                }
            } else if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_MIDDLE)) {
                mouseMiddle = false;
            } else {
                // Move paths
                int dx = event.getX() - dragX;
                int dy = event.getY() - dragY;
                pathDrawer.setOffset(dx, dy);
            }

            if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_LEFT)) {
                anchor = new Point2D(mousePosition);
                released = false;
            } else if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
                released = true;

                Point2D end = new Point2D(mousePosition);

                // On release
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
                        // The last curve is bezier
                        if (path.size() <= 2) {
                            // TODO This is buggy
                            path.removeLast();
                            path.add(new QuadraticCurve(anchor, mousePosition, new Point2D(cp1)));

                        } else {
                            if (curve.getType() == CurveType.CUBIC_BEZIER) {
                                path.removeLast();
                                CubicCurve cubicCurve = (CubicCurve) curve;
                                path.add(new QuadraticCurve(curve.getEnd(), mousePosition, cubicCurve.getControl1()));
                            } else {
                                path.removeLast();
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
                    // First drag event
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

            if (event.isAnyKeyDown(KeyEvent.VK_CTRL_RIGHT)||event.isAnyKeyDown(KeyEvent.VK_CTRL_LEFT)) {
                keyCtrl = true;
            } else if (event.isAnyKeyUp(KeyEvent.VK_CTRL_RIGHT)||event.isAnyKeyDown(KeyEvent.VK_CTRL_LEFT)) {
                keyCtrl = false;
            }

            if (event.isKeyUp(KeyEvent.VK_ESC)) {
                if (!keyCtrl && !path.getCurves().isEmpty()) {
                    // Stop working with path
                    path.removeLast();
                    paths.add(path);
                    path = new Path2D();
                } else {
                    // Clear all paths
                    paths.clear();
                    paths.add(path);
                    path = new Path2D();
                }
            }

            if (keyCtrl && event.isKeyUp(KeyEvent.VK_Z)) {
                undo();
            }

            if (keyCtrl && event.isKeyUp(KeyEvent.VK_S)) {
                if (path.getCurves().size() <= 1) {
                    return;
                }
                Path2D copy = new Path2D();
                copy.getCurves().addAll(path.getCurves());
                copy.removeLast();

                System.out.println(pathExporter.writeString(path));
            }
        }

        private void undo() {
            // Undo the last curve
            path.removeLast();
            if (!path.isEmpty()) {
                DataCurve lastCurve = path.getCurves().get(path.getCurves().size() - 1);
                if (lastCurve.getType() == CurveType.CUBIC_BEZIER) {
                    path.removeLast();
                    CubicCurve cubic = (CubicCurve) lastCurve;
                    path.add(new QuadraticCurve(cubic.getStart(), mousePosition, cubic.getControl1()));
                } else {
                    lastCurve.setEnd(mousePosition);
                }
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
                        if (DRAW_CONTROL_POINTS) {
                            pathDrawer.drawLine(g, qCurve.getP0(), qCurve.getP1());
                            pathDrawer.drawControlPoint(g, qCurve.getP1());
                        }
                        // Start
                        pathDrawer.drawPoint(g, qCurve.getP0());
                        // End
                        pathDrawer.drawPoint(g, qCurve.getP2());
                        break;
                    case CUBIC_BEZIER:
                        CubicBezier cCurve = (CubicBezier) c.getData();
                        pathDrawer.drawCurve(g, cCurve);

                        if (DRAW_CONTROL_POINTS) {
                            // Draw control points
                            pathDrawer.drawLine(g, cCurve.getP2(), cCurve.getP3());
                            pathDrawer.drawLine(g, cCurve.getP0(), cCurve.getP1());

                            pathDrawer.drawControlPoint(g, cCurve.getP1());
                            pathDrawer.drawControlPoint(g, cCurve.getP2());
                        }
                        // Start
                        pathDrawer.drawPoint(g, cCurve.getP0());
                        // End
                        pathDrawer.drawPoint(g, cCurve.getP3());
                        break;
                }
            }
        }

        @Override
        public void dropFiles(int x, int y, List<File> files) {
            for (File file : files) {
                try {
                    List<Path2D> paths = pathImporter.read(file);
                    this.paths.addAll(paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}