/*
 * PathGenerator.java
 *
 * Created on November 22, 2007, 6:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.harium.etyl.geometry.path.fit;

import Jama.Matrix;
import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;


/**
 * This class is used to generate instances of Path objects. Typical usage is
 * <code>new PathFinder().solve(samplePoints)</code>, where
 * <code>samplePoints</code> is an array of Point2D sample points. This will
 * return a Path2D object consisting of Bezier curves.
 *
 * @author Erik
 * @see #solve(Point2D[])
 */
public class LeastSquareSolver implements CurveFitSolver {

    private static final double FIT_TOLERANCE = 0.5;
    private static final double[][] BEZIER3COEFF = new double[][]{{-1.0, 3.0, -3.0, 1.0},
            {3.0, -6.0, 3.0, 0.0},
            {-3.0, 3.0, 0.0, 0.0},
            {1.0, 0.0, 0.0, 0.0}
    };

    private double[] initialT;
    private int start, end;
    private int farthestPointIdx;
    private double[] t;
    private double[][] controlPoints = new double[4][2];
    private double[][] position = new double[4][2];
    private double[][] velocity = new double[3][2];
    private double fitToleranceSq = FIT_TOLERANCE * FIT_TOLERANCE;
    private Path2D path;

    public LeastSquareSolver() {

    }

    /**
     * Generates a Path2D from an array of sample points
     *
     * @throws InterruptedException , CancelledException
     */
    public Path2D solve(Point2D[] samplePoints) {
        start = 0;
        end = samplePoints.length - 1;
        int n = samplePoints.length;
        if (n < 2) {
            throw new IllegalArgumentException(
                    "Argument samplePoints must contain at least 2 points");
        }
        path = new Path2D();
        path.moveTo(samplePoints[0].x, samplePoints[0].y);
        initialParametrization(samplePoints);
        fitBezierSeq(0, n - 1, samplePoints);
        return path;
    }

    private void initialParametrization(Point2D[] samplePoints) {
        int n = end - start + 1;
        t = new double[n];
        t[0] = 0.0;
        if (n == samplePoints.length) {
            for (int i = 0; i + start < end; i++) {
                t[i + 1] = t[i] + samplePoints[i].distance(samplePoints[i + 1]);
            }
            for (int i = 0; i < n; i++) {
                t[i] /= t[n - 1];
            }
            t[n - 1] = 1.0;
            initialT = t.clone();
        } else {
            double offset = initialT[start];
            double totalTime = initialT[end] - offset;
            for (int i = start; i < end; i++) {
                t[i - start] = (initialT[i] - offset) / totalTime;
            }
            t[n - 1] = 1.0;
        }
    }

    private void fitBezierSeq(final int start, final int end, Point2D[] samplePoints) {
        assert end > start;
        this.start = start;
        this.end = end;
        initialParametrization(samplePoints);
        int n = end - start + 1;
        if (n == 2) {
            // number of points == 2.
            // Fit 2 points with a line.
            path.lineTo(samplePoints[end].x, samplePoints[end].y);
            return;
        }
        // Adjust the initial parametrization to the current segments
        if (n == 3) {
            // number of points == 3.
            // Fit 3 points with a quad curve.
            fitQuad(start, end, samplePoints);
            path.quadTo(
                    controlPoints[1][0],
                    controlPoints[1][1],
                    controlPoints[2][0],
                    controlPoints[2][1]);
            return;
        }
        // number of points >= 4
        // Fit 4 or more points with a cubic curve.
        fitCubic(samplePoints);
        // Find the point farthest from the curve.
        farthestPointIdx = start;
        double maxDistSq = 0.0;
        for (int i = start + 1; i < end; i++) {
            double distSq = samplePoints[i].distanceSq(position(t[i - start]));
            if (distSq > maxDistSq) {
                maxDistSq = distSq;
                farthestPointIdx = i;
            }
        }

        if (maxDistSq > fitToleranceSq) {
            int splitIdx = farthestPointIdx;
            fitBezierSeq(start, splitIdx, samplePoints);
            fitBezierSeq(splitIdx, end, samplePoints);
        } else {
            path.curveTo(
                    controlPoints[1][0],
                    controlPoints[1][1],
                    controlPoints[2][0],
                    controlPoints[2][1],
                    controlPoints[3][0],
                    controlPoints[3][1]);
        }
    }

    /**
     * Pre-cond: end = start + 2, i.e., there are only three points to fit.
     *
     * @param start
     * @param end
     */
    private void fitQuad(final int start, final int end, Point2D[] samplePoints) {
        controlPoints[0][0] = samplePoints[start].x;
        controlPoints[0][1] = samplePoints[start].y;
        controlPoints[2][0] = samplePoints[end].x;
        controlPoints[2][1] = samplePoints[end].y;
        controlPoints[1][0] = (
                samplePoints[start + 1].x / (1 - t[1]) / t[1]
                        - (1 - t[1]) / t[1] * controlPoints[0][0]
                        - t[1] / (1 - t[1]) * controlPoints[2][0]) / 2.0;
        controlPoints[1][1] = (
                samplePoints[start + 1].y / (1 - t[1]) / t[1]
                        - (1 - t[1]) / t[1] * controlPoints[0][1]
                        - t[1] / (1 - t[1]) * controlPoints[2][1]) / 2.0;
    }

    private void fitCubic(Point2D[] samplePoints) {
        boolean doneFitting = false;
        double previousResidual; // = Double.MAX_VALUE;
        double residual = Double.MAX_VALUE;
        while (!doneFitting) {
            leastSquareFit(samplePoints);
            previousResidual = residual;
            residual = reparametrize(samplePoints);
            if (residual < 0.95 * previousResidual && residual > 0.25) {
            } else {
                doneFitting = true;
            }
        }
    }

    private void leastSquareFit(Point2D[] samplePoints) {
        Matrix matrixA = getWxTxB12();
        Matrix matrixB = getMatrixWxDminusWxTxB03xP03t(samplePoints);
        Matrix matrixX = matrixA.solve(matrixB);
        double[][] controlPoints12 = matrixX.getArray();
        controlPoints[0][0] = samplePoints[start].x;
        controlPoints[0][1] = samplePoints[start].y;
        controlPoints[1][0] = controlPoints12[0][0];
        controlPoints[1][1] = controlPoints12[0][1];
        controlPoints[2][0] = controlPoints12[1][0];
        controlPoints[2][1] = controlPoints12[1][1];
        controlPoints[3][0] = samplePoints[end].x;
        controlPoints[3][1] = samplePoints[end].y;

        // recompute the position array
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) { // row
                position[j][i] = 0.0;
                for (int k = 0; k < 4; k++) { // column
                    position[j][i] +=
                            BEZIER3COEFF[j][k] * controlPoints[k][i];
                }
            }
        }
        // recompute the velocity array
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) { // row
                velocity[j][i] = 0.0;
                for (int k = 0; k < 4; k++) { // column
                    velocity[j][i] +=
                            (3 - j) * BEZIER3COEFF[j][k] * controlPoints[k][i];
                }
            }
        }
    }

    private double reparametrize(Point2D[] samplePoints) {
        assert t[0] == 0.0;
        assert t[end - start] == 1.0;
        double res = 0.0;
        double tHat;
        Point2D p;
        Point2D v;
        Point2D pHat;
        for (int i = 1; i + start < end; i++) {
            p = position(t[i]);
            v = velocity(t[i]);
            tHat = t[i] + ((samplePoints[i + start].x - p.x) * v.x +
                    (samplePoints[i + start].y - p.y) * v.y)
                    / v.distanceSq(0.0, 0.0);
            pHat = position(tHat);
            double distBefore = samplePoints[i + start].distanceSq(p);
            double distAfter = samplePoints[i + start].distanceSq(pHat);
            if (distAfter < distBefore) {
                t[i] = tHat;
                res += distAfter;
            } else {
                res += distBefore;
            }
        }
        return res;
    }

    private Point2D position(double t) {
        double x = 0.0;
        double y = 0.0;
        for (int k = 0; k < 4; k++) {
            x *= t;
            x += position[k][0];
        }
        for (int k = 0; k < 4; k++) {
            y *= t;
            y += position[k][1];
        }
        return new Point2D(x, y);
    }

    private Point2D velocity(double t) {
        double x = 0.0;
        double y = 0.0;
        for (int k = 0; k < 3; k++) {
            x *= t;
            x += velocity[k][0];
        }
        for (int k = 0; k < 3; k++) {
            y *= t;
            y += velocity[k][1];
        }
        return new Point2D(x, y);
    }

    private Matrix getWxTxB12() {
        double[][] a = new double[t.length][2];
        int n = t.length - 1;
        for (int i = 0; i <= n; i++) {
            double wi = weight(n, i);
            for (int j = 0; j < 2; j++) {
                a[i][j] = 0.0;
                for (int k = 0; k < 4; k++) {
                    a[i][j] *= t[i];
                    a[i][j] += BEZIER3COEFF[k][j + 1];
                }
                a[i][j] *= wi;
            }
        }
        return new Matrix(a);
    }

    private Matrix getMatrixWxDminusWxTxB03xP03t(Point2D[] samplePoints) {
        int n = end - start;
        double[][] b03xP03 = new double[4][2];
        double a[][] = new double[n + 1][2];
        for (int k = 0; k < 4; k++) {
            b03xP03[k][0] = BEZIER3COEFF[k][0] * samplePoints[start].x +
                    BEZIER3COEFF[k][3] * samplePoints[end].x;
            b03xP03[k][1] = BEZIER3COEFF[k][0] * samplePoints[start].y +
                    BEZIER3COEFF[k][3] * samplePoints[end].y;
        }
        for (int i = 0; i <= n; i++) {
            double wi = weight(n, i);
            double txb03P03X = 0;
            double txb03P03Y = 0;
            for (int j = 0; j < 4; j++) {
                txb03P03X *= t[i];
                txb03P03Y *= t[i];
                txb03P03X += b03xP03[j][0];
                txb03P03Y += b03xP03[j][1];
            }
            a[i][0] = wi * (samplePoints[start + i].x - txb03P03X);
            a[i][1] = wi * (samplePoints[start + i].y - txb03P03Y);
        }
        return new Matrix(a);
    }

    private double weight(int n, final int i) {
        return Math.abs(n - 2.0 * i);
        // return 1.0;
    }


}
