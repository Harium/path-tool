package com.harium.etyl.geometry.path.importer;

import com.harium.etyl.geometry.Path2D;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PathImporter {

    List<Path2D> read(String data);

    List<Path2D> read(File file) throws IOException;
}
