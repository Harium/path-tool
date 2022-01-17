package com.harium.etyl.geometry.path.exporter;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.path.ShapeAttributes;

import java.util.List;

public interface PathExporter {

    String writeString(Path2D path);

    String writeString(Path2D path, ShapeAttributes style);

    String writeString(List<Path2D> path);

    String writeString(List<Path2D> path, List<ShapeAttributes> attributes);

}
