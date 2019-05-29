package com.harium.etyl.geometry.path.fit;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.Point2D;

public interface CurveFitSolver {

    Path2D solve(Point2D[] samplePoints);

}
