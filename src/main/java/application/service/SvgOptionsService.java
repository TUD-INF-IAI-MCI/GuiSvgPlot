package application.service;

import javafx.collections.FXCollections;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SvgOptionsService {

    private static final SvgOptionsService INSTANCE = new SvgOptionsService();
    private ResourceBundle bundle;

    private SvgOptionsService() {
    }

    public static SvgOptionsService getInstance() {
        return INSTANCE;
    }

    public StringConverter<DiagramType> getDiagramTypConverter() {
        return new StringConverter<DiagramType>() {
            @Override
            public String toString(DiagramType diagramType) {
                return bundle.getString(diagramType.toString());
            }

            @Override
            public DiagramType fromString(String string) {
                DiagramType diagramType = DiagramType.FunctionPlot;
                for (DiagramType dt : FXCollections.observableArrayList(DiagramType.values())) {
                    if (this.toString(dt).equals(string)) {
                        diagramType = dt;
                    }
                }
                return diagramType;
            }
        };
    }

    public void buildSVG(SvgPlotOptions svgPlotOptions) {
        svgPlotOptions.finalizeOptions();

        SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
        try {
            creator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildSVG(SvgPlotOptions svgPlotOptions, WebView webView_svg) {
        File svg = svgPlotOptions.getOutput();

        if (svg == null) {
            Path svgPath = Paths.get(System.getProperty("user.home") + "/svgPlot/svg.svg");

            svg = new File(svgPath.toString());
            svgPlotOptions.setOutput(svg);
        }
        svgPlotOptions.finalizeOptions();

        SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
        try {
            creator.run();

            /* show created svg */
            BufferedReader br = new BufferedReader(new FileReader(svg));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            WebEngine webEngine = webView_svg.getEngine();
            webEngine.loadContent(sb.toString());

            // accessibility
            Path descPath = Paths.get(svg.getParentFile().getPath() + "/" + svg.getName().replace(".svg", "_desc.html"));
            String description = loadDescription(descPath.toString());
            webView_svg.setAccessibleHelp(bundle.getString("preview") + ": " + description);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads description file and and builds a String.
     *
     * @param path Path to the description HTML document
     * @return the description as string
     */
    private String loadDescription(String path) {
        String desc = "";

        try {
            File descFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(descFile);

            NodeList divs = doc.getElementsByTagName("div");
            for (int temp = 0; temp < divs.getLength(); temp++) {
                desc = desc.concat(divs.item(temp).getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
