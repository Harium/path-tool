package com.harium.etyl.geometry.path.svg.importer;

import com.harium.etyl.geometry.Path2D;
import com.harium.etyl.geometry.path.importer.PathImporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SVGImporter implements PathImporter {

    private SVGPathParser svgParser = new SVGPathParser();

    @Override
    public List<Path2D> read(String svgData) {
        List<Path2D> list = new ArrayList<>();

        Document doc = Jsoup.parse(svgData);
        // Parse paths only
        Elements pathElements = doc.getElementsByTag("path");
        for (Element element: pathElements) {
            String d = element.attr("d");
            List<Path2D> paths = svgParser.parseData(d);
            list.addAll(paths);
        }

        return list;
    }

    @Override
    public List<Path2D> read(File file) throws IOException {
        StringBuilder sb;
        BufferedReader buf = null;
        try {
            InputStream is = new FileInputStream(file);
            buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
        } finally {
            if (buf != null) {
                buf.close();
            }
        }

        String data = sb.toString();
        return read(data);
    }

}
