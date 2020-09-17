package com.harium.etyl.geometry.path.exporter;

import com.harium.etyl.geometry.Path2D;

public interface PathExporter {

    String writeString(Path2D path);

    String writeString(Path2D path, PathOptions options);

}
